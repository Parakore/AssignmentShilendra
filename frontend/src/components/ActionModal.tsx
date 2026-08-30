import React, { useState } from 'react';

interface ActionModalProps {
  isOpen: boolean;
  onClose: () => void;
  action: string;
  applicationNumber: string;
  onConfirm: (comment: string) => Promise<void>;
}

export const ActionModal: React.FC<ActionModalProps> = ({
  isOpen,
  onClose,
  action,
  applicationNumber,
  onConfirm,
}) => {
  const [comment, setComment] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      await onConfirm(comment);
      setComment('');
      onClose();
    } catch (err: any) {
      setError(err.message || 'Failed to process workflow action');
    } finally {
      setLoading(false);
    }
  };

  const getActionConfig = () => {
    switch (action.toUpperCase()) {
      case 'VERIFY':
        return {
          title: 'Verify Application (Junior Engineer)',
          desc: 'Verify site feasibility, road cut dimensions, and forward to Executive Engineer for final approval.',
          btnText: 'Verify & Forward',
          btnClass: 'bg-indigo-600 hover:bg-indigo-700 text-white',
          commentPlaceholder: 'Enter verification remarks (e.g., site inspected, dimensions confirmed)...',
          commentRequired: false,
        };
      case 'SEND_BACK':
        return {
          title: 'Send Back to Applicant',
          desc: 'Return the application to the applicant for corrections. Status will return to APPLIED so the applicant can edit.',
          btnText: 'Send Back for Corrections',
          btnClass: 'bg-amber-600 hover:bg-amber-700 text-white',
          commentPlaceholder: 'Specify the required corrections or missing documents...',
          commentRequired: true,
        };
      case 'APPROVE':
        return {
          title: 'Grant Road Cutting Permission (Executive Engineer)',
          desc: 'Issue official permission to proceed with road excavation per approved dimensions and schedule.',
          btnText: 'Approve & Issue Permission',
          btnClass: 'bg-emerald-600 hover:bg-emerald-700 text-white',
          commentPlaceholder: 'Enter approval conditions or special instructions (optional)...',
          commentRequired: false,
        };
      case 'REJECT':
        return {
          title: 'Reject Application (Executive Engineer)',
          desc: 'Permanently reject this road cutting permission request.',
          btnText: 'Reject Application',
          btnClass: 'bg-rose-600 hover:bg-rose-700 text-white',
          commentPlaceholder: 'State reason for rejection (e.g., VIP route, recent road resurfacing warranty)...',
          commentRequired: true,
        };
      case 'CANCEL':
        return {
          title: 'Cancel Application (Applicant)',
          desc: 'Withdraw your road cutting permission application. This action cannot be undone.',
          btnText: 'Confirm Cancellation',
          btnClass: 'bg-slate-700 hover:bg-slate-800 text-white',
          commentPlaceholder: 'Reason for cancellation (optional)...',
          commentRequired: false,
        };
      default:
        return {
          title: `Perform Action: ${action}`,
          desc: `Execute workflow action ${action} on application ${applicationNumber}.`,
          btnText: 'Submit Action',
          btnClass: 'bg-blue-600 hover:bg-blue-700 text-white',
          commentPlaceholder: 'Remarks...',
          commentRequired: false,
        };
    }
  };

  const config = getActionConfig();

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-200">
        <div className="flex justify-between items-start mb-4">
          <div>
            <h3 className="text-lg font-bold text-slate-900">{config.title}</h3>
            <p className="text-xs text-slate-500 font-mono mt-0.5">
              Application: <span className="font-semibold text-slate-800">{applicationNumber}</span>
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600 text-xl font-bold p-1"
          >
            ×
          </button>
        </div>

        <p className="text-sm text-slate-600 mb-4 bg-slate-50 p-3 rounded-lg border border-slate-200">
          {config.desc}
        </p>

        {error && (
          <div className="mb-4 p-3 bg-rose-50 border border-rose-200 rounded-lg text-xs text-rose-700">
            <span className="font-bold">Error:</span> {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Remarks / Comments {config.commentRequired ? <span className="text-rose-600">*</span> : '(Optional)'}
            </label>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              required={config.commentRequired}
              rows={3}
              placeholder={config.commentPlaceholder}
              className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 outline-none"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              disabled={loading}
              className="px-4 py-2 text-xs font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-lg transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className={`px-4 py-2 text-xs font-semibold rounded-lg shadow-sm transition flex items-center space-x-2 ${config.btnClass}`}
            >
              {loading && <div className="w-3.5 h-3.5 border-2 border-white border-t-transparent rounded-full animate-spin" />}
              <span>{config.btnText}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
