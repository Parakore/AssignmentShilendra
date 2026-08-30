import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';
import { ApplicationDetail } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { FeeBreakdownCard } from '../components/FeeBreakdownCard';
import { StatusTimeline } from '../components/StatusTimeline';
import { ActionModal } from '../components/ActionModal';
import { ArrowLeft, Edit3, XCircle, CheckCircle, RotateCcw, AlertOctagon, MapPin, User, Calendar, Ruler, FileText } from 'lucide-react';

interface ApplicationDetailPageProps {
  application: ApplicationDetail;
  onBack: () => void;
  onApplicationUpdated: (app: ApplicationDetail) => void;
  onEditRequested?: (app: ApplicationDetail) => void;
}

export const ApplicationDetailPage: React.FC<ApplicationDetailPageProps> = ({
  application,
  onBack,
  onApplicationUpdated,
  onEditRequested,
}) => {
  const { currentPersona, getRequestInfo } = useAuth();

  const [activeModalAction, setActiveModalAction] = useState<string | null>(null);
  const [loadingAction, setLoadingAction] = useState<boolean>(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const allowedActions = application.allowedActions || [];

  const handleActionConfirm = async (comment: string) => {
    if (!activeModalAction) return;

    setLoadingAction(true);
    setErrorMsg(null);

    try {
      const updated = await api.applyAction(
        application.applicationNumber,
        activeModalAction,
        comment,
        undefined,
        getRequestInfo()
      );
      onApplicationUpdated(updated);
    } catch (err: any) {
      setErrorMsg(err.message || 'Action failed');
      throw err;
    } finally {
      setLoadingAction(false);
    }
  };

  const isApplicant = currentPersona.roleCode === 'APPLICANT';
  const isVerifier = currentPersona.roleCode === 'VERIFIER';
  const isApprover = currentPersona.roleCode === 'APPROVER';

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      
      {/* Back Button & Top Banner */}
      <div className="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <button
          onClick={onBack}
          className="inline-flex items-center space-x-1.5 text-xs font-semibold text-slate-600 hover:text-slate-900 bg-white px-3 py-1.5 rounded-lg border border-slate-200 shadow-xs self-start"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to List</span>
        </button>

        {/* Action Controls Bar */}
        <div className="flex flex-wrap items-center gap-2">
          
          {/* APPLICANT ACTIONS */}
          {isApplicant && application.status === 'APPLIED' && (
            <>
              {onEditRequested && (
                <button
                  type="button"
                  onClick={() => onEditRequested(application)}
                  className="px-3.5 py-1.5 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 border border-indigo-200 text-xs font-bold rounded-lg shadow-xs transition flex items-center space-x-1.5"
                >
                  <Edit3 className="w-3.5 h-3.5" />
                  <span>Edit & Resubmit</span>
                </button>
              )}
              <button
                type="button"
                onClick={() => setActiveModalAction('CANCEL')}
                className="px-3.5 py-1.5 bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 text-xs font-bold rounded-lg shadow-xs transition flex items-center space-x-1.5"
              >
                <XCircle className="w-3.5 h-3.5" />
                <span>Cancel Application</span>
              </button>
            </>
          )}

          {/* VERIFIER (Junior Engineer) ACTIONS */}
          {isVerifier && application.status === 'APPLIED' && (
            <button
              type="button"
              onClick={() => setActiveModalAction('VERIFY')}
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold rounded-lg shadow-xs transition flex items-center space-x-1.5"
            >
              <CheckCircle className="w-4 h-4" />
              <span>Verify & Forward to EE</span>
            </button>
          )}

          {isVerifier && application.status === 'PENDING_APPROVAL' && (
            <button
              type="button"
              onClick={() => setActiveModalAction('SEND_BACK')}
              className="px-4 py-2 bg-amber-600 hover:bg-amber-700 text-white text-xs font-bold rounded-lg shadow-xs transition flex items-center space-x-1.5"
            >
              <RotateCcw className="w-4 h-4" />
              <span>Send Back to Applicant</span>
            </button>
          )}

          {/* APPROVER (Executive Engineer) ACTIONS */}
          {isApprover && application.status === 'PENDING_APPROVAL' && (
            <>
              <button
                type="button"
                onClick={() => setActiveModalAction('APPROVE')}
                className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-lg shadow-xs transition flex items-center space-x-1.5"
              >
                <CheckCircle className="w-4 h-4" />
                <span>Approve Permission</span>
              </button>

              <button
                type="button"
                onClick={() => setActiveModalAction('REJECT')}
                className="px-4 py-2 bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold rounded-lg shadow-xs transition flex items-center space-x-1.5"
              >
                <AlertOctagon className="w-4 h-4" />
                <span>Reject Permission</span>
              </button>
            </>
          )}

        </div>
      </div>

      {errorMsg && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-800 mb-6 flex justify-between items-center">
          <span><strong>Action Failed:</strong> {errorMsg}</span>
          <button onClick={() => setErrorMsg(null)} className="font-bold ml-2">✕</button>
        </div>
      )}

      {/* Main Grid: Details Left, Timeline & Fee Right */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        {/* Left Column: Application Data */}
        <div className="lg:col-span-7 space-y-6">
          
          {/* Header Card */}
          <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
            <div className="flex flex-wrap justify-between items-start gap-3 pb-4 border-b border-slate-100">
              <div>
                <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">
                  Application Reference
                </span>
                <h3 className="text-lg sm:text-xl font-mono font-bold text-slate-900">
                  {application.applicationNumber}
                </h3>
              </div>
              <StatusBadge status={application.status} />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-4 text-xs">
              <div>
                <span className="text-slate-500 block mb-0.5">Municipal Corporation</span>
                <span className="font-semibold text-slate-800 capitalize">{application.tenantId}</span>
              </div>
              <div>
                <span className="text-slate-500 block mb-0.5">Applicant Category</span>
                <span className="font-semibold text-slate-800">{application.applicantType}</span>
              </div>
              <div>
                <span className="text-slate-500 block mb-0.5">Applicant Name</span>
                <span className="font-semibold text-slate-800">{application.applicantName}</span>
              </div>
              <div>
                <span className="text-slate-500 block mb-0.5">Registered Mobile</span>
                <span className="font-semibold text-slate-800 font-mono">{application.applicantMobile}</span>
              </div>
              {application.applicantEmail && (
                <div className="sm:col-span-2">
                  <span className="text-slate-500 block mb-0.5">Email Address</span>
                  <span className="font-semibold text-slate-800">{application.applicantEmail}</span>
                </div>
              )}
            </div>
          </div>

          {/* Road Cut & Location Card */}
          <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm space-y-4">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 pb-2 border-b border-slate-100 flex items-center space-x-1.5">
              <Ruler className="w-4 h-4 text-emerald-600" />
              <span>Cutting Dimensions & Road Type</span>
            </h4>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-xs">
              <div className="bg-slate-50 p-3 rounded-lg border border-slate-200">
                <span className="text-slate-500 block">Road Type</span>
                <span className="font-bold text-slate-800 text-sm">{application.roadTypeName || application.roadType}</span>
              </div>
              <div className="bg-slate-50 p-3 rounded-lg border border-slate-200">
                <span className="text-slate-500 block">Length</span>
                <span className="font-bold text-slate-800 text-sm mono-num">{application.lengthInMeters} m</span>
              </div>
              <div className="bg-slate-50 p-3 rounded-lg border border-slate-200">
                <span className="text-slate-500 block">Width</span>
                <span className="font-bold text-slate-800 text-sm mono-num">{application.widthInMeters} m</span>
              </div>
              <div className="bg-emerald-50 p-3 rounded-lg border border-emerald-200">
                <span className="text-emerald-700 block font-medium">Billed Area</span>
                <span className="font-black text-emerald-800 text-sm mono-num">{application.areaInSqm} sq.m</span>
              </div>
            </div>

            <div className="pt-2">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
                <div>
                  <span className="text-slate-500 block mb-0.5">Proposed Start Date</span>
                  <span className="font-semibold text-slate-800 font-mono">{application.proposedStartDate}</span>
                </div>
                <div>
                  <span className="text-slate-500 block mb-0.5">Duration of Digging</span>
                  <span className="font-semibold text-slate-800">{application.durationInDays} Days</span>
                </div>
              </div>
            </div>

            <div className="pt-2 border-t border-slate-100">
              <span className="text-slate-500 text-xs block mb-1">Site Location</span>
              <div className="flex items-center space-x-1.5 text-xs font-semibold text-slate-800 bg-slate-50 p-2.5 rounded-lg border border-slate-200">
                <MapPin className="w-4 h-4 text-rose-500 flex-shrink-0" />
                <span>{application.location}</span>
              </div>
            </div>

            {application.description && (
              <div className="pt-2">
                <span className="text-slate-500 text-xs block mb-1">Work Description</span>
                <p className="text-xs text-slate-700 bg-slate-50 p-2.5 rounded-lg border border-slate-200">
                  {application.description}
                </p>
              </div>
            )}
          </div>

          {/* Transparent Fee Summary */}
          <div>
            <FeeBreakdownCard
              calculation={application.calculation}
            />
          </div>

        </div>

        {/* Right Column: Status Timeline */}
        <div className="lg:col-span-5 space-y-6">
          <div className="bg-white rounded-xl border border-slate-200 p-6 shadow-sm">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-500 pb-3 mb-4 border-b border-slate-100 flex items-center space-x-1.5">
              <FileText className="w-4 h-4 text-indigo-600" />
              <span>Permission Lifecycle Timeline</span>
            </h4>
            <StatusTimeline timeline={application.timeline} />
          </div>
        </div>

      </div>

      {/* Action Dialog Modal */}
      {activeModalAction && (
        <ActionModal
          isOpen={true}
          onClose={() => setActiveModalAction(null)}
          action={activeModalAction}
          applicationNumber={application.applicationNumber}
          onConfirm={handleActionConfirm}
        />
      )}

    </div>
  );
};
