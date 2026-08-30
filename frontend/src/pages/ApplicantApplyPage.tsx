import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';
import { CalculationResult, ApplicationCreatePayload, ApplicationDetail } from '../types';
import { FeeBreakdownCard } from '../components/FeeBreakdownCard';
import { Send, Sparkles, CheckCircle2, RotateCcw, AlertTriangle } from 'lucide-react';

interface ApplicantApplyPageProps {
  onApplicationCreated: (app: ApplicationDetail) => void;
  editingApplication?: ApplicationDetail | null;
  onCancelEdit?: () => void;
}

const ROAD_TYPES = [
  { code: 'BT', label: 'Bituminous (BT) - Standard Tar Road' },
  { code: 'CC', label: 'Cement Concrete (CC) - High Durability' },
  { code: 'WBM', label: 'Water Bound Macadam (WBM) - Stone Aggregate' },
  { code: 'KUTCHA', label: 'Kutcha (Earthen) - Inactive for Cutting' },
];

const APPLICANT_TYPES = [
  { code: 'PRIVATE', label: 'Private Individual / Resident' },
  { code: 'COMMERCIAL', label: 'Commercial Entity / Telecom / Utility' },
  { code: 'GOVERNMENT_AGENCY', label: 'Government Agency / Department (Fee Exempt)' },
];

export const ApplicantApplyPage: React.FC<ApplicantApplyPageProps> = ({
  onApplicationCreated,
  editingApplication,
  onCancelEdit,
}) => {
  const { currentPersona, getRequestInfo } = useAuth();

  // Helper for tomorrow's date string YYYY-MM-DD
  const getDefaultStartDate = () => {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().split('T')[0];
  };

  const getMinDate = () => {
    return new Date().toISOString().split('T')[0];
  };

  // Form State
  const [formData, setFormData] = useState<ApplicationCreatePayload>(() => {
    if (editingApplication) {
      return {
        tenantId: editingApplication.tenantId,
        roadType: editingApplication.roadType,
        lengthInMeters: editingApplication.lengthInMeters,
        widthInMeters: editingApplication.widthInMeters,
        durationInDays: editingApplication.durationInDays,
        applicantType: editingApplication.applicantType,
        proposedStartDate: editingApplication.proposedStartDate,
        applicantName: editingApplication.applicantName,
        applicantMobile: editingApplication.applicantMobile,
        applicantEmail: editingApplication.applicantEmail || '',
        location: editingApplication.location,
        description: editingApplication.description || '',
      };
    }

    // Draft survival: check localStorage
    const savedDraft = localStorage.getItem(`rcp_draft_${currentPersona.tenantId}`);
    if (savedDraft) {
      try {
        const parsed = JSON.parse(savedDraft);
        return {
          ...parsed,
          tenantId: currentPersona.tenantId,
          applicantName: parsed.applicantName || currentPersona.name,
          applicantMobile: parsed.applicantMobile || currentPersona.mobile,
        };
      } catch (e) {}
    }

    return {
      tenantId: currentPersona.tenantId,
      roadType: 'BT',
      lengthInMeters: 12.5,
      widthInMeters: 1.2,
      durationInDays: 6,
      applicantType: 'PRIVATE',
      proposedStartDate: getDefaultStartDate(),
      applicantName: currentPersona.name,
      applicantMobile: currentPersona.mobile,
      applicantEmail: 'applicant@example.com',
      location: 'Rajpur Road, Near Clock Tower',
      description: 'Laying underground optic fibre line',
    };
  });

  // Calculation State
  const [calculation, setCalculation] = useState<CalculationResult | null>(null);
  const [calcLoading, setCalcLoading] = useState<boolean>(false);
  const [calcError, setCalcError] = useState<string | null>(null);

  // Submission State
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({});

  // Sync tenant ID when persona switches (unless editing)
  useEffect(() => {
    if (!editingApplication) {
      setFormData((prev) => ({
        ...prev,
        tenantId: currentPersona.tenantId,
        applicantName: prev.applicantName === 'Aarav Sharma' || prev.applicantName === 'Pooja Verma' ? currentPersona.name : prev.applicantName,
        applicantMobile: prev.applicantMobile === '9990000001' || prev.applicantMobile === '9888000002' ? currentPersona.mobile : prev.applicantMobile,
      }));
    }
  }, [currentPersona, editingApplication]);

  // Draft survival auto-saver
  useEffect(() => {
    if (!editingApplication) {
      localStorage.setItem(`rcp_draft_${currentPersona.tenantId}`, JSON.stringify(formData));
    }
  }, [formData, currentPersona.tenantId, editingApplication]);

  // Validate form client-side
  const validateForm = () => {
    const errs: Record<string, string> = {};
    if (!formData.roadType) errs.roadType = 'Road type is required';
    if (!formData.lengthInMeters || formData.lengthInMeters <= 0) errs.lengthInMeters = 'Length must be > 0';
    if (!formData.widthInMeters || formData.widthInMeters <= 0) errs.widthInMeters = 'Width must be > 0';
    if (!formData.durationInDays || formData.durationInDays <= 0) errs.durationInDays = 'Duration must be at least 1 day';
    if (!formData.proposedStartDate) errs.proposedStartDate = 'Start date is required';
    if (!formData.applicantName?.trim()) errs.applicantName = 'Applicant name is required';
    if (!formData.applicantMobile?.trim()) errs.applicantMobile = 'Mobile number is required';
    if (!formData.location?.trim()) errs.location = 'Location is required';

    setValidationErrors(errs);
    return Object.keys(errs).length === 0;
  };

  // Debounced fee calculation
  const triggerCalculation = useCallback(async () => {
    if (
      !formData.roadType ||
      !formData.lengthInMeters ||
      formData.lengthInMeters <= 0 ||
      !formData.widthInMeters ||
      formData.widthInMeters <= 0 ||
      !formData.durationInDays ||
      formData.durationInDays <= 0 ||
      !formData.proposedStartDate
    ) {
      return;
    }

    setCalcLoading(true);
    setCalcError(null);

    try {
      const result = await api.calculateFee(
        {
          tenantId: formData.tenantId,
          roadType: formData.roadType,
          lengthInMeters: Number(formData.lengthInMeters),
          widthInMeters: Number(formData.widthInMeters),
          durationInDays: Number(formData.durationInDays),
          applicantType: formData.applicantType,
          proposedStartDate: formData.proposedStartDate,
        },
        getRequestInfo()
      );
      setCalculation(result);
    } catch (err: any) {
      setCalcError(err.message || 'Fee calculation failed');
      setCalculation(null);
    } finally {
      setCalcLoading(false);
    }
  }, [formData, getRequestInfo]);

  useEffect(() => {
    const timer = setTimeout(() => {
      triggerCalculation();
    }, 350); // 350ms debounce
    return () => clearTimeout(timer);
  }, [triggerCalculation]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'number' ? (value === '' ? '' : parseFloat(value)) : value,
    }));
  };

  const handleQuickFill = (type: 'A' | 'B' | 'GOVT' | 'KUTCHA') => {
    if (type === 'A') {
      setFormData({
        tenantId: 'dehradun',
        roadType: 'BT',
        lengthInMeters: 12.5,
        widthInMeters: 1.2,
        durationInDays: 6,
        applicantType: 'PRIVATE',
        proposedStartDate: getDefaultStartDate(),
        applicantName: currentPersona.name,
        applicantMobile: currentPersona.mobile,
        applicantEmail: 'applicant@dehradun.gov.in',
        location: 'Rajpur Road, Near Clock Tower',
        description: 'Fibre trenching project (Worked Example A: Expected ₹24,485)',
      });
    } else if (type === 'B') {
      setFormData({
        tenantId: 'haridwar',
        roadType: 'BT',
        lengthInMeters: 12.5,
        widthInMeters: 1.2,
        durationInDays: 6,
        applicantType: 'PRIVATE',
        proposedStartDate: getDefaultStartDate(),
        applicantName: currentPersona.name,
        applicantMobile: currentPersona.mobile,
        applicantEmail: 'applicant@haridwar.gov.in',
        location: 'Har Ki Pauri Main Corridor',
        description: 'Trenching with Haridwar rate override (Worked Example B: Expected ₹27,480)',
      });
    } else if (type === 'GOVT') {
      setFormData({
        tenantId: currentPersona.tenantId,
        roadType: 'CC',
        lengthInMeters: 10.0,
        widthInMeters: 2.0,
        durationInDays: 10,
        applicantType: 'GOVERNMENT_AGENCY',
        proposedStartDate: getDefaultStartDate(),
        applicantName: 'Uttarakhand Jal Sansthan',
        applicantMobile: currentPersona.mobile,
        applicantEmail: 'jalsansthan@uk.gov.in',
        location: 'Civil Lines Water Main',
        description: 'Emergency water main repair',
      });
    } else if (type === 'KUTCHA') {
      setFormData({
        tenantId: currentPersona.tenantId,
        roadType: 'KUTCHA',
        lengthInMeters: 10.0,
        widthInMeters: 2.0,
        durationInDays: 5,
        applicantType: 'PRIVATE',
        proposedStartDate: getDefaultStartDate(),
        applicantName: currentPersona.name,
        applicantMobile: currentPersona.mobile,
        applicantEmail: 'applicant@example.com',
        location: 'Village Link Road',
        description: 'Testing inactive road type error handling',
      });
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validateForm()) return;

    setSubmitting(true);
    setSubmitError(null);

    try {
      if (editingApplication) {
        // Resubmission / Edit flow
        const updated = await api.applyAction(
          editingApplication.applicationNumber,
          'EDIT',
          'Application details updated and resubmitted by applicant',
          formData,
          getRequestInfo()
        );
        onApplicationCreated(updated);
      } else {
        // Create new application
        const created = await api.createApplication(formData, getRequestInfo());
        // Clear saved draft
        localStorage.removeItem(`rcp_draft_${currentPersona.tenantId}`);
        onApplicationCreated(created);
      }
    } catch (err: any) {
      setSubmitError(err.message || 'Failed to submit application');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      
      {/* Page Header */}
      <div className="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-slate-900">
            {editingApplication ? 'Edit & Resubmit Application' : 'Apply for Road Cutting Permission'}
          </h2>
          <p className="text-xs sm:text-sm text-slate-600 mt-0.5">
            Submit dimensions and schedule for municipal engineering review & fee calculation.
          </p>
        </div>

        {/* Quick Test Fillers */}
        <div className="flex flex-wrap gap-2 items-center">
          <span className="text-xs font-semibold text-slate-500">Quick Scenarios:</span>
          <button
            type="button"
            onClick={() => handleQuickFill('A')}
            className="px-2.5 py-1 text-xs font-medium rounded bg-emerald-50 text-emerald-700 hover:bg-emerald-100 border border-emerald-200 transition"
          >
            Example A (DDN BT)
          </button>
          <button
            type="button"
            onClick={() => handleQuickFill('B')}
            className="px-2.5 py-1 text-xs font-medium rounded bg-indigo-50 text-indigo-700 hover:bg-indigo-100 border border-indigo-200 transition"
          >
            Example B (HDW BT)
          </button>
          <button
            type="button"
            onClick={() => handleQuickFill('GOVT')}
            className="px-2.5 py-1 text-xs font-medium rounded bg-blue-50 text-blue-700 hover:bg-blue-100 border border-blue-200 transition"
          >
            Govt Agency
          </button>
          <button
            type="button"
            onClick={() => handleQuickFill('KUTCHA')}
            className="px-2.5 py-1 text-xs font-medium rounded bg-rose-50 text-rose-700 hover:bg-rose-100 border border-rose-200 transition"
          >
            Inactive (Kutcha)
          </button>
        </div>
      </div>

      {editingApplication && (
        <div className="mb-6 p-4 bg-amber-50 border border-amber-200 rounded-xl flex items-start justify-between">
          <div className="flex items-start space-x-3">
            <AlertTriangle className="w-5 h-5 text-amber-600 mt-0.5 flex-shrink-0" />
            <div>
              <h4 className="text-sm font-bold text-amber-900">Editing Application: {editingApplication.applicationNumber}</h4>
              <p className="text-xs text-amber-700 mt-0.5">
                This application was returned with remarks. Update the requested details below and submit to return the file for verification.
              </p>
            </div>
          </div>
          {onCancelEdit && (
            <button
              onClick={onCancelEdit}
              className="text-xs font-semibold text-amber-800 hover:text-amber-900 underline"
            >
              Cancel Edit
            </button>
          )}
        </div>
      )}

      {submitError && (
        <div className="mb-6 p-4 bg-rose-50 border border-rose-200 rounded-xl text-xs sm:text-sm text-rose-800 flex items-start space-x-3">
          <span className="font-bold text-rose-600 text-base leading-none">✕</span>
          <div>
            <span className="font-bold block">Submission Failed:</span>
            <span>{submitError}</span>
          </div>
        </div>
      )}

      {/* Main Grid: Form Left, Fee Preview Right */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Form Column */}
        <div className="lg:col-span-7">
          <form onSubmit={handleSubmit} className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-sm space-y-5">
            
            {/* Section: Applicant Details */}
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3 pb-1 border-b border-slate-100">
                1. Applicant Information
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Applicant Name <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="applicantName"
                    value={formData.applicantName}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none"
                    placeholder="e.g. Ramesh Kumar"
                  />
                  {validationErrors.applicantName && (
                    <p className="text-[11px] text-rose-600 mt-1">{validationErrors.applicantName}</p>
                  )}
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Mobile Number <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="tel"
                    name="applicantMobile"
                    value={formData.applicantMobile}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none"
                    placeholder="e.g. 9990000001"
                  />
                  {validationErrors.applicantMobile && (
                    <p className="text-[11px] text-rose-600 mt-1">{validationErrors.applicantMobile}</p>
                  )}
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Email Address
                  </label>
                  <input
                    type="email"
                    name="applicantEmail"
                    value={formData.applicantEmail || ''}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none"
                    placeholder="applicant@example.com"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Applicant Category <span className="text-rose-500">*</span>
                  </label>
                  <select
                    name="applicantType"
                    value={formData.applicantType}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none bg-white"
                  >
                    {APPLICANT_TYPES.map((t) => (
                      <option key={t.code} value={t.code}>
                        {t.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>

            {/* Section: Road Cut Specifications */}
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3 pb-1 border-b border-slate-100">
                2. Road Cutting Specifications
              </h3>
              
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="sm:col-span-2">
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Road Surface Type <span className="text-rose-500">*</span>
                  </label>
                  <select
                    name="roadType"
                    value={formData.roadType}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none bg-white font-medium"
                  >
                    {ROAD_TYPES.map((rt) => (
                      <option key={rt.code} value={rt.code}>
                        {rt.label}
                      </option>
                    ))}
                  </select>
                  {validationErrors.roadType && (
                    <p className="text-[11px] text-rose-600 mt-1">{validationErrors.roadType}</p>
                  )}
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Length in Metres (m) <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="number"
                    step="0.1"
                    min="0.1"
                    name="lengthInMeters"
                    value={formData.lengthInMeters}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none font-mono"
                    placeholder="12.5"
                  />
                  {validationErrors.lengthInMeters && (
                    <p className="text-[11px] text-rose-600 mt-1">{validationErrors.lengthInMeters}</p>
                  )}
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Width in Metres (m) <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="number"
                    step="0.1"
                    min="0.1"
                    name="widthInMeters"
                    value={formData.widthInMeters}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none font-mono"
                    placeholder="1.2"
                  />
                  {validationErrors.widthInMeters && (
                    <p className="text-[11px] text-rose-600 mt-1">{validationErrors.widthInMeters}</p>
                  )}
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Duration in Days <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="number"
                    min="1"
                    max="365"
                    name="durationInDays"
                    value={formData.durationInDays}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none font-mono"
                    placeholder="6"
                  />
                  {validationErrors.durationInDays && (
                    <p className="text-[11px] text-rose-600 mt-1">{validationErrors.durationInDays}</p>
                  )}
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Proposed Start Date <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="date"
                    min={getMinDate()}
                    name="proposedStartDate"
                    value={formData.proposedStartDate}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none font-mono"
                  />
                  {validationErrors.proposedStartDate && (
                    <p className="text-[11px] text-rose-600 mt-1">{validationErrors.proposedStartDate}</p>
                  )}
                </div>
              </div>
            </div>

            {/* Section: Location & Description */}
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500 mb-3 pb-1 border-b border-slate-100">
                3. Site Location & Work Details
              </h3>
              
              <div className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Exact Location / Street Address <span className="text-rose-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none"
                    placeholder="e.g. Rajpur Road, Near Clock Tower, Ward 4"
                  />
                  {validationErrors.location && (
                    <p className="text-[11px] text-rose-600 mt-1">{validationErrors.location}</p>
                  )}
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Work Purpose & Description
                  </label>
                  <textarea
                    rows={2}
                    name="description"
                    value={formData.description || ''}
                    onChange={handleChange}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 outline-none"
                    placeholder="e.g. Laying underground optical fibre conduit..."
                  />
                </div>
              </div>
            </div>

            {/* Submit Action */}
            <div className="pt-2">
              <button
                type="submit"
                disabled={submitting || calcLoading}
                className="w-full py-3 px-4 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-bold rounded-xl shadow-sm transition flex items-center justify-center space-x-2 disabled:opacity-50"
              >
                {submitting ? (
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                ) : (
                  <Send className="w-4 h-4" />
                )}
                <span>{editingApplication ? 'Update & Resubmit File' : 'Submit Application'}</span>
              </button>
            </div>

          </form>
        </div>

        {/* Live Fee Breakdown Column */}
        <div className="lg:col-span-5 space-y-4">
          <FeeBreakdownCard
            calculation={calculation}
            loading={calcLoading}
            error={calcError}
          />

          <div className="bg-slate-50 border border-slate-200 rounded-xl p-4 text-xs text-slate-600 space-y-2">
            <h4 className="font-bold text-slate-800 flex items-center space-x-1.5">
              <Sparkles className="w-3.5 h-3.5 text-emerald-600" />
              <span>Municipal Rate Policy Note</span>
            </h4>
            <p>
              • <strong>Area:</strong> Dimensions are multiplied and rounded up to the nearest whole square metre (<code className="bg-slate-200 px-1 py-0.5 rounded text-[11px]">ceil(L × W)</code>).
            </p>
            <p>
              • <strong>Urgency Surcharge:</strong> Applications starting within 3 days attract a 10% surcharge on permission fees.
            </p>
            <p>
              • <strong>Security Deposit:</strong> 25% of restoration charge subject to city minimum floor deposit. Refunded post-restoration verification.
            </p>
          </div>
        </div>

      </div>

    </div>
  );
};
