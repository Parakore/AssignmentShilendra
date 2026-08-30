import React from 'react';
import { CalculationResult } from '../types';

interface FeeBreakdownCardProps {
  calculation: CalculationResult | null;
  loading?: boolean;
  error?: string | null;
}

export const FeeBreakdownCard: React.FC<FeeBreakdownCardProps> = ({
  calculation,
  loading = false,
  error = null,
}) => {
  const formatCurrency = (val: number | undefined) => {
    if (val === undefined || val === null) return '₹0';
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(val);
  };

  if (loading) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 p-5 shadow-sm">
        <div className="flex items-center space-x-3 mb-4">
          <div className="w-5 h-5 border-2 border-emerald-600 border-t-transparent rounded-full animate-spin"></div>
          <h3 className="text-sm font-semibold text-slate-800">Calculating Fee Preview...</h3>
        </div>
        <div className="space-y-2.5 animate-pulse">
          <div className="h-4 bg-slate-100 rounded w-3/4"></div>
          <div className="h-4 bg-slate-100 rounded w-1/2"></div>
          <div className="h-4 bg-slate-100 rounded w-5/6"></div>
          <div className="h-8 bg-slate-200 rounded w-full mt-4"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-amber-50 rounded-xl border border-amber-200 p-5">
        <div className="flex items-start space-x-3">
          <div className="p-1 rounded bg-amber-100 text-amber-700 font-bold text-sm">!</div>
          <div>
            <h3 className="text-sm font-semibold text-amber-900">Fee Calculation Notice</h3>
            <p className="text-xs text-amber-700 mt-1">{error}</p>
            <p className="text-xs text-amber-600 mt-1 italic">
              Please enter valid dimensions and an active road type to view the live fee breakdown.
            </p>
          </div>
        </div>
      </div>
    );
  }

  if (!calculation) {
    return (
      <div className="bg-slate-50 rounded-xl border border-slate-200 p-5 text-center">
        <p className="text-sm text-slate-500">
          Enter dimensions, road type, and dates to see the live calculated fee breakdown.
        </p>
      </div>
    );
  }

  const details = calculation.breakdownDetails || {};

  return (
    <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
      <div className="bg-slate-900 text-white px-5 py-3.5 flex justify-between items-center">
        <div>
          <h3 className="text-sm font-semibold tracking-wide uppercase text-slate-300">
            Fee Estimate & Breakdown
          </h3>
          <p className="text-xs text-slate-400">Official Municipal Schedule of Rates</p>
        </div>
        {calculation.reviewRef && (
          <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-mono font-bold bg-emerald-950 text-emerald-300 border border-emerald-800">
            Ref: {calculation.reviewRef}
          </span>
        )}
      </div>

      <div className="p-5 space-y-4 text-sm">
        {/* Area Info */}
        <div className="flex justify-between items-center pb-3 border-b border-slate-100">
          <div>
            <span className="font-medium text-slate-800">Billable Road Area</span>
            <p className="text-xs text-slate-500">
              Product: {details.rawProductArea || ''} m² → Ceil rounded up to whole m²
            </p>
          </div>
          <span className="text-base font-bold text-slate-900 mono-num">
            {calculation.areaInSqm} sq.m
          </span>
        </div>

        {/* Breakdown Items */}
        <div className="space-y-3">
          {/* 1. Restoration Charge */}
          <div className="flex justify-between items-start">
            <div>
              <div className="flex items-center space-x-1.5">
                <span className="text-slate-700 font-medium">1. Restoration Charge</span>
              </div>
              <p className="text-xs text-slate-500">
                {calculation.areaInSqm} m² × ₹{details.restorationRatePerSqm || '...'}/m² ({details.roadTypeName || 'Road'})
              </p>
            </div>
            <span className="font-semibold text-slate-800 mono-num">
              {formatCurrency(calculation.restorationCharge)}
            </span>
          </div>

          {/* 2. Permission Fee */}
          <div className="flex justify-between items-start">
            <div>
              <div className="flex items-center space-x-1.5">
                <span className="text-slate-700 font-medium">2. Permission Fee</span>
                {details.isGovtAgency && (
                  <span className="text-[10px] px-1.5 py-0.2 rounded bg-blue-100 text-blue-800 font-semibold">
                    Govt Agency Exempt (₹0)
                  </span>
                )}
              </div>
              <p className="text-xs text-slate-500">
                {details.isGovtAgency
                  ? 'Exempt for Government Agency'
                  : `${calculation.areaInSqm} m² × ₹${details.permissionRatePerSqmPerDay || '...'}/day × duration`}
              </p>
            </div>
            <span className="font-semibold text-slate-800 mono-num">
              {formatCurrency(calculation.permissionFee)}
            </span>
          </div>

          {/* 3. Urgency Surcharge */}
          <div className="flex justify-between items-start">
            <div>
              <div className="flex items-center space-x-1.5">
                <span className="text-slate-700 font-medium">3. Urgency Surcharge</span>
                {details.isUrgent ? (
                  <span className="text-[10px] px-1.5 py-0.2 rounded bg-amber-100 text-amber-800 font-semibold">
                    &lt; 3 days ({details.urgencySurchargePercent}%)
                  </span>
                ) : (
                  <span className="text-[10px] px-1.5 py-0.2 rounded bg-slate-100 text-slate-600">
                    Standard (₹0)
                  </span>
                )}
              </div>
              <p className="text-xs text-slate-500">
                {details.isUrgent
                  ? `${details.urgencySurchargePercent}% on permission fee (Start date < 3 days away)`
                  : 'Start date ≥ 3 days away (No surcharge)'}
              </p>
            </div>
            <span className="font-semibold text-slate-800 mono-num">
              {formatCurrency(calculation.urgencySurcharge)}
            </span>
          </div>

          {/* 4. Security Deposit */}
          <div className="flex justify-between items-start">
            <div>
              <div className="flex items-center space-x-1.5">
                <span className="text-slate-700 font-medium">4. Security Deposit</span>
                <span className="text-[10px] px-1.5 py-0.2 rounded bg-emerald-100 text-emerald-800 font-semibold">
                  Refundable
                </span>
              </div>
              <p className="text-xs text-slate-500">
                max(Floor ₹{details.minSecurityDepositFloor || 0}, {details.securityDepositPercent || 25}% of Restoration ₹{formatCurrency(details.calculatedDepositFromPercent ? Number(details.calculatedDepositFromPercent) : 0)})
              </p>
            </div>
            <span className="font-semibold text-slate-800 mono-num">
              {formatCurrency(calculation.securityDeposit)}
            </span>
          </div>
        </div>

        {/* Total Highlight */}
        <div className="pt-3 border-t-2 border-slate-200 flex justify-between items-center bg-slate-50 -mx-5 -mb-5 p-5 mt-4">
          <div>
            <span className="text-base font-bold text-slate-900 block">Total Payable Amount</span>
            <span className="text-xs text-slate-500">Includes refundable security deposit</span>
          </div>
          <div className="text-right">
            <span className="text-2xl font-black text-emerald-700 mono-num">
              {formatCurrency(calculation.totalAmount)}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
