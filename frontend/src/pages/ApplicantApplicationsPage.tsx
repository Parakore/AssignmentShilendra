import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/client';
import { ApplicationDetail } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { Search, RefreshCw, Eye, Calendar, MapPin, IndianRupee, Layers } from 'lucide-react';

interface ApplicantApplicationsPageProps {
  onSelectApplication: (app: ApplicationDetail) => void;
  onNewApplication: () => void;
}

export const ApplicantApplicationsPage: React.FC<ApplicantApplicationsPageProps> = ({
  onSelectApplication,
  onNewApplication,
}) => {
  const { currentPersona, getRequestInfo } = useAuth();

  const [applications, setApplications] = useState<ApplicationDetail[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchNumber, setSearchNumber] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const fetchApplications = async () => {
    setLoading(true);
    setError(null);
    try {
      const q = searchNumber.trim();
      const is10DigitMobile = /^[0-9]{10}$/.test(q);

      const results = await api.searchApplications(
        {
          mobileNumber: is10DigitMobile ? q : currentPersona.mobile,
          status: statusFilter === 'ALL' ? undefined : statusFilter,
          applicationNumber: q && !is10DigitMobile ? q : undefined,
        },
        getRequestInfo()
      );
      setApplications(results);
    } catch (err: any) {
      setError(err.message || 'Failed to load applications');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchApplications();
  }, [currentPersona, statusFilter]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchApplications();
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
      
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-slate-900">My Road Cutting Permissions</h2>
          <p className="text-xs sm:text-sm text-slate-600 mt-0.5">
            Applications filed by <span className="font-semibold text-slate-800">{currentPersona.name}</span> ({currentPersona.mobile})
          </p>
        </div>
        <button
          onClick={onNewApplication}
          className="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-lg shadow-sm transition self-start sm:self-auto"
        >
          + New Permission Request
        </button>
      </div>

      {/* Filters Bar */}
      <div className="bg-white rounded-xl border border-slate-200 p-4 shadow-sm mb-6 flex flex-col md:flex-row items-center justify-between gap-4">
        
        {/* Status Pills */}
        <div className="flex flex-wrap gap-1.5 w-full md:w-auto">
          {['ALL', 'APPLIED', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'CANCELLED'].map((st) => (
            <button
              key={st}
              onClick={() => setStatusFilter(st)}
              className={`px-3 py-1 text-xs font-semibold rounded-md transition ${
                statusFilter === st
                  ? 'bg-slate-900 text-white shadow-xs'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {st === 'ALL' ? 'All Applications' : st.replace('_', ' ')}
            </button>
          ))}
        </div>

        {/* Search Input */}
        <form onSubmit={handleSearchSubmit} className="flex items-center gap-2 w-full md:w-auto">
          <div className="relative flex-1 md:w-64">
            <Search className="w-3.5 h-3.5 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              placeholder="Search App Number..."
              value={searchNumber}
              onChange={(e) => setSearchNumber(e.target.value)}
              className="w-full pl-9 pr-3 py-1.5 text-xs rounded-lg border border-slate-300 focus:ring-2 focus:ring-emerald-500 outline-none"
            />
          </div>
          <button
            type="submit"
            className="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-semibold rounded-lg transition flex items-center space-x-1"
          >
            <span>Filter</span>
          </button>
          <button
            type="button"
            onClick={fetchApplications}
            title="Refresh List"
            className="p-1.5 text-slate-500 hover:text-slate-700 rounded-lg hover:bg-slate-100 transition"
          >
            <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </form>

      </div>

      {/* Error state */}
      {error && (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-xl text-xs text-rose-800 mb-6 flex justify-between items-center">
          <span>{error}</span>
          <button onClick={fetchApplications} className="underline font-bold text-rose-900 ml-3">
            Retry
          </button>
        </div>
      )}

      {/* Loading state */}
      {loading && (
        <div className="bg-white rounded-xl border border-slate-200 p-12 text-center shadow-sm">
          <div className="w-8 h-8 border-3 border-emerald-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
          <p className="text-sm font-medium text-slate-600">Loading your applications...</p>
        </div>
      )}

      {/* Empty State */}
      {!loading && applications.length === 0 && (
        <div className="bg-white rounded-xl border border-dashed border-slate-300 p-12 text-center shadow-sm">
          <Layers className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <h3 className="text-base font-bold text-slate-800">No applications found</h3>
          <p className="text-xs text-slate-500 mt-1 max-w-sm mx-auto">
            {statusFilter !== 'ALL' || searchNumber
              ? 'No permissions match your search or status filter. Try clearing filters.'
              : 'You have not submitted any road cutting permissions yet in this municipal corporation.'}
          </p>
          <button
            onClick={onNewApplication}
            className="mt-4 px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold rounded-lg shadow-sm transition"
          >
            Create Your First Application
          </button>
        </div>
      )}

      {/* Cards List */}
      {!loading && applications.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {applications.map((app) => (
            <div
              key={app.id}
              onClick={() => onSelectApplication(app)}
              className="bg-white rounded-xl border border-slate-200 p-5 shadow-xs hover:shadow-md hover:border-emerald-300 transition-all cursor-pointer flex flex-col justify-between"
            >
              <div>
                <div className="flex justify-between items-start mb-3 gap-2">
                  <span className="font-mono text-xs font-bold text-slate-900 bg-slate-100 px-2 py-0.5 rounded border border-slate-200">
                    {app.applicationNumber}
                  </span>
                  <StatusBadge status={app.status} />
                </div>

                <div className="space-y-2 text-xs text-slate-600 mt-3">
                  <div className="flex items-center space-x-1.5 text-slate-700">
                    <MapPin className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                    <span className="truncate font-medium">{app.location}</span>
                  </div>

                  <div className="flex items-center space-x-1.5 text-slate-600">
                    <Calendar className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                    <span>Starts: {app.proposedStartDate} ({app.durationInDays} days)</span>
                  </div>

                  <div className="text-slate-500 text-[11px] pt-1 border-t border-slate-100 flex justify-between">
                    <span>Road: <strong className="text-slate-700">{app.roadTypeName || app.roadType}</strong></span>
                    <span>Area: <strong className="text-slate-700">{app.areaInSqm} m²</strong></span>
                  </div>
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between">
                <div>
                  <span className="text-[10px] uppercase tracking-wider text-slate-400 block">Total Fee</span>
                  <span className="text-base font-bold text-emerald-700 mono-num">
                    {formatCurrency(app.calculation?.totalAmount || 0)}
                  </span>
                </div>
                <button
                  type="button"
                  className="px-2.5 py-1 text-xs font-semibold text-emerald-700 bg-emerald-50 hover:bg-emerald-100 rounded-md transition flex items-center space-x-1"
                >
                  <Eye className="w-3.5 h-3.5" />
                  <span>View Details</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

    </div>
  );
};
