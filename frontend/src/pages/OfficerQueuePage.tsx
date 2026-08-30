import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';
import { ApplicationDetail } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { ActionModal } from '../components/ActionModal';
import {
  Search,
  RefreshCw,
  Eye,
  CheckCircle,
  AlertOctagon,
  RotateCcw,
  UserCheck,
  Building,
  Calendar,
  Layers,
} from 'lucide-react';

interface OfficerQueuePageProps {
  onSelectApplication: (app: ApplicationDetail) => void;
}

export const OfficerQueuePage: React.FC<OfficerQueuePageProps> = ({ onSelectApplication }) => {
  const { currentPersona, getRequestInfo } = useAuth();

  const [applications, setApplications] = useState<ApplicationDetail[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Default filter: VERIFIER defaults to 'APPLIED', APPROVER defaults to 'PENDING_APPROVAL'
  const [statusFilter, setStatusFilter] = useState<string>(() => {
    if (currentPersona.roleCode === 'VERIFIER') return 'APPLIED';
    if (currentPersona.roleCode === 'APPROVER') return 'PENDING_APPROVAL';
    return 'ALL';
  });

  const [searchQuery, setSearchQuery] = useState<string>('');
  const [selectedActionApp, setSelectedActionApp] = useState<{ appNum: string; action: string } | null>(null);

  const isVerifier = currentPersona.roleCode === 'VERIFIER';
  const isApprover = currentPersona.roleCode === 'APPROVER';

  const fetchQueue = async () => {
    setLoading(true);
    setError(null);
    try {
      const q = searchQuery.trim();
      const is10DigitMobile = /^[0-9]{10}$/.test(q);

      const results = await api.searchApplications(
        {
          status: statusFilter === 'ALL' ? undefined : statusFilter,
          applicationNumber: q && !is10DigitMobile ? q : undefined,
          mobileNumber: is10DigitMobile ? q : undefined,
        },
        getRequestInfo()
      );
      setApplications(results);
    } catch (err: any) {
      setError(err.message || 'Failed to load officer queue');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // When persona switches, reset filter sensibly
    if (currentPersona.roleCode === 'VERIFIER') {
      setStatusFilter('APPLIED');
    } else if (currentPersona.roleCode === 'APPROVER') {
      setStatusFilter('PENDING_APPROVAL');
    } else {
      setStatusFilter('ALL');
    }
  }, [currentPersona]);

  useEffect(() => {
    fetchQueue();
  }, [currentPersona, statusFilter]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    fetchQueue();
  };

  const handleActionConfirm = async (comment: string) => {
    if (!selectedActionApp) return;
    try {
      await api.applyAction(
        selectedActionApp.appNum,
        selectedActionApp.action,
        comment,
        undefined,
        getRequestInfo()
      );
      setSelectedActionApp(null);
      await fetchQueue();
    } catch (err: any) {
      alert(err.message || 'Action failed');
    }
  };

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(val);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      
      {/* Top Banner */}
      <div className="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <div className="flex items-center space-x-2">
            <h2 className="text-xl sm:text-2xl font-bold text-slate-900">
              Municipal Officer Action Queue
            </h2>
            <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-indigo-100 text-indigo-800">
              {currentPersona.roleName} Desk
            </span>
          </div>
          <p className="text-xs sm:text-sm text-slate-600 mt-1">
            Logged in as <span className="font-semibold text-slate-800">{currentPersona.name}</span> • Serving{' '}
            <strong className="text-slate-800 capitalize">{currentPersona.tenantName}</strong> (Tenant: <code className="font-mono text-xs">{currentPersona.tenantId}</code>)
          </p>
        </div>

        <div className="flex items-center space-x-3">
          <button
            onClick={fetchQueue}
            className="px-3.5 py-2 bg-white hover:bg-slate-100 text-slate-700 text-xs font-semibold rounded-lg border border-slate-300 shadow-xs transition flex items-center space-x-1.5"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
            <span>Refresh Queue</span>
          </button>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white rounded-xl border border-slate-200 p-4 shadow-sm mb-6 flex flex-col lg:flex-row items-center justify-between gap-4">
        
        {/* Status Filters */}
        <div className="flex flex-wrap gap-1.5 w-full lg:w-auto">
          {[
            { id: 'ALL', label: 'All Records' },
            { id: 'APPLIED', label: 'Applied (Needs JE Verification)' },
            { id: 'PENDING_APPROVAL', label: 'Pending Approval (EE Desk)' },
            { id: 'APPROVED', label: 'Approved' },
            { id: 'REJECTED', label: 'Rejected' },
            { id: 'CANCELLED', label: 'Cancelled' },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setStatusFilter(tab.id)}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
                statusFilter === tab.id
                  ? 'bg-slate-900 text-white shadow-xs'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Search */}
        <form onSubmit={handleSearch} className="flex items-center gap-2 w-full lg:w-auto">
          <div className="relative flex-1 lg:w-72">
            <Search className="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Search App Number or Mobile..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-9 pr-3 py-1.5 text-xs rounded-lg border border-slate-300 focus:ring-2 focus:ring-emerald-500 outline-none"
            />
          </div>
          <button
            type="submit"
            className="px-3 py-1.5 bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold rounded-lg transition"
          >
            Search
          </button>
        </form>

      </div>

      {/* Error state */}
      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-800 mb-6 flex justify-between items-center">
          <span>{error}</span>
          <button onClick={fetchQueue} className="underline font-bold text-rose-900 ml-3">
            Retry
          </button>
        </div>
      )}

      {/* Loading state */}
      {loading && (
        <div className="bg-white rounded-xl border border-slate-200 p-12 text-center shadow-sm">
          <div className="w-8 h-8 border-3 border-indigo-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
          <p className="text-sm font-medium text-slate-600">Loading municipal queue...</p>
        </div>
      )}

      {/* Empty State */}
      {!loading && applications.length === 0 && (
        <div className="bg-white rounded-xl border border-dashed border-slate-300 p-12 text-center shadow-sm">
          <Layers className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <h3 className="text-base font-bold text-slate-800">No applications in this queue</h3>
          <p className="text-xs text-slate-500 mt-1 max-w-md mx-auto">
            {statusFilter !== 'ALL'
              ? `There are currently no files in '${statusFilter}' status for ${currentPersona.tenantName}. Switch filters or check other tabs.`
              : `No files found for tenant ${currentPersona.tenantId}.`}
          </p>
        </div>
      )}

      {/* Queue Table for Desktop & Cards for Mobile */}
      {!loading && applications.length > 0 && (
        <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-200 text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 font-semibold uppercase tracking-wider text-[11px]">
                <tr>
                  <th className="px-4 py-3.5">Application #</th>
                  <th className="px-4 py-3.5">Applicant & Category</th>
                  <th className="px-4 py-3.5">Road & Cut Specs</th>
                  <th className="px-4 py-3.5">Schedule</th>
                  <th className="px-4 py-3.5">Fee Amount</th>
                  <th className="px-4 py-3.5">Current Status</th>
                  <th className="px-4 py-3.5 text-right">Role Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 bg-white">
                {applications.map((app) => {
                  const allowed = app.allowedActions || [];

                  return (
                    <tr key={app.id} className="hover:bg-slate-50/80 transition-colors">
                      {/* App Number & Location */}
                      <td className="px-4 py-3.5 align-top">
                        <span className="font-mono font-bold text-slate-900 block">
                          {app.applicationNumber}
                        </span>
                        <span className="text-[11px] text-slate-500 truncate block max-w-xs mt-0.5">
                          {app.location}
                        </span>
                      </td>

                      {/* Applicant */}
                      <td className="px-4 py-3.5 align-top">
                        <span className="font-medium text-slate-800 block">{app.applicantName}</span>
                        <span className="text-[11px] text-slate-500 font-mono block">{app.applicantMobile}</span>
                        <span className="text-[10px] px-1.5 py-0.2 rounded bg-slate-100 text-slate-600 font-semibold inline-block mt-0.5">
                          {app.applicantType}
                        </span>
                      </td>

                      {/* Road & Specs */}
                      <td className="px-4 py-3.5 align-top">
                        <span className="font-medium text-slate-800 block">
                          {app.roadTypeName || app.roadType}
                        </span>
                        <span className="text-[11px] text-slate-500 block">
                          {app.lengthInMeters}m × {app.widthInMeters}m (<strong className="text-slate-700">{app.areaInSqm} m²</strong>)
                        </span>
                      </td>

                      {/* Schedule */}
                      <td className="px-4 py-3.5 align-top">
                        <span className="font-medium text-slate-700 block">{app.proposedStartDate}</span>
                        <span className="text-[11px] text-slate-500 block">{app.durationInDays} Digging Days</span>
                      </td>

                      {/* Fee */}
                      <td className="px-4 py-3.5 align-top">
                        <span className="font-bold text-emerald-700 mono-num block text-sm">
                          {formatCurrency(app.calculation?.totalAmount || 0)}
                        </span>
                        <span className="text-[10px] text-slate-400 block">
                          Deposit: {formatCurrency(app.calculation?.securityDeposit || 0)}
                        </span>
                      </td>

                      {/* Status */}
                      <td className="px-4 py-3.5 align-top">
                        <StatusBadge status={app.status} />
                      </td>

                      {/* Dynamic Action Buttons Driven by Server Allowed Actions */}
                      <td className="px-4 py-3.5 align-top text-right space-x-1.5 whitespace-nowrap">
                        
                        <button
                          onClick={() => onSelectApplication(app)}
                          className="px-2.5 py-1 text-xs font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-md transition inline-flex items-center space-x-1"
                        >
                          <Eye className="w-3.5 h-3.5" />
                          <span>View</span>
                        </button>

                        {/* Allowed Action: VERIFY */}
                        {allowed.includes('VERIFY') && (
                          <button
                            onClick={() => setSelectedActionApp({ appNum: app.applicationNumber, action: 'VERIFY' })}
                            className="px-2.5 py-1 text-xs font-semibold text-white bg-indigo-600 hover:bg-indigo-700 rounded-md transition shadow-xs inline-flex items-center space-x-1"
                            title="Verify application and forward to Executive Engineer"
                          >
                            <CheckCircle className="w-3.5 h-3.5" />
                            <span>Verify</span>
                          </button>
                        )}

                        {/* Allowed Action: SEND_BACK */}
                        {allowed.includes('SEND_BACK') && (
                          <button
                            onClick={() => setSelectedActionApp({ appNum: app.applicationNumber, action: 'SEND_BACK' })}
                            className="px-2.5 py-1 text-xs font-semibold text-amber-800 bg-amber-100 hover:bg-amber-200 rounded-md transition border border-amber-300 inline-flex items-center space-x-1"
                            title="Send back to applicant for revisions"
                          >
                            <RotateCcw className="w-3.5 h-3.5" />
                            <span>Send Back</span>
                          </button>
                        )}

                        {/* Allowed Action: APPROVE */}
                        {allowed.includes('APPROVE') && (
                          <button
                            onClick={() => setSelectedActionApp({ appNum: app.applicationNumber, action: 'APPROVE' })}
                            className="px-2.5 py-1 text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 rounded-md transition shadow-xs inline-flex items-center space-x-1"
                            title="Grant road cutting permission"
                          >
                            <CheckCircle className="w-3.5 h-3.5" />
                            <span>Approve</span>
                          </button>
                        )}

                        {/* Allowed Action: REJECT */}
                        {allowed.includes('REJECT') && (
                          <button
                            onClick={() => setSelectedActionApp({ appNum: app.applicationNumber, action: 'REJECT' })}
                            className="px-2.5 py-1 text-xs font-semibold text-white bg-rose-600 hover:bg-rose-700 rounded-md transition shadow-xs inline-flex items-center space-x-1"
                            title="Reject permission request"
                          >
                            <AlertOctagon className="w-3.5 h-3.5" />
                            <span>Reject</span>
                          </button>
                        )}

                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Action Dialog Modal */}
      {selectedActionApp && (
        <ActionModal
          isOpen={true}
          onClose={() => setSelectedActionApp(null)}
          action={selectedActionApp.action}
          applicationNumber={selectedActionApp.appNum}
          onConfirm={handleActionConfirm}
        />
      )}

    </div>
  );
};
