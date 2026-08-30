import React from 'react';
import { PERSONAS, useAuth } from '../context/AuthContext';
import { ShieldCheck, UserCheck, MapPin, Layers, FileText, CheckCircle2 } from 'lucide-react';

interface HeaderProps {
  activeTab: 'apply' | 'my-applications' | 'officer-queue';
  setActiveTab: (tab: 'apply' | 'my-applications' | 'officer-queue') => void;
}

export const Header: React.FC<HeaderProps> = ({ activeTab, setActiveTab }) => {
  const { currentPersona, setPersona } = useAuth();

  const handlePersonaChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selected = PERSONAS.find(p => p.id === e.target.value);
    if (selected) {
      setPersona(selected);
      // Auto-switch tabs if appropriate for role
      if (selected.roleCode === 'APPLICANT' && activeTab === 'officer-queue') {
        setActiveTab('my-applications');
      } else if ((selected.roleCode === 'VERIFIER' || selected.roleCode === 'APPROVER') && activeTab !== 'officer-queue') {
        setActiveTab('officer-queue');
      }
    }
  };

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-30 shadow-xs">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between py-3 sm:py-0 sm:h-16 gap-3">
          
          {/* Brand & City Indicator */}
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-lg bg-emerald-600 flex items-center justify-center text-white shadow-sm font-bold">
                <ShieldCheck className="w-6 h-6" />
              </div>
              <div>
                <div className="flex items-center space-x-2">
                  <h1 className="text-base sm:text-lg font-bold text-slate-900 leading-tight">
                    Road Cutting Permission
                  </h1>
                  <span className="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-emerald-100 text-emerald-800 uppercase tracking-wide">
                    RCP Portal
                  </span>
                </div>
                <div className="flex items-center text-xs text-slate-500 space-x-1 mt-0.5">
                  <MapPin className="w-3.5 h-3.5 text-emerald-600" />
                  <span className="font-semibold text-slate-700">{currentPersona.tenantName}</span>
                  <span className="text-slate-300">•</span>
                  <span>Tenant: <span className="font-mono text-slate-600">{currentPersona.tenantId}</span></span>
                </div>
              </div>
            </div>
          </div>

          {/* Navigation & Persona Switcher */}
          <div className="flex flex-wrap items-center gap-2 sm:gap-4 justify-between sm:justify-end">
            {/* Tabs */}
            <nav className="flex space-x-1 bg-slate-100 p-1 rounded-lg text-xs font-semibold">
              <button
                onClick={() => setActiveTab('apply')}
                className={`px-3 py-1.5 rounded-md transition-all flex items-center space-x-1.5 ${
                  activeTab === 'apply'
                    ? 'bg-white text-emerald-700 shadow-xs font-bold'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <FileText className="w-3.5 h-3.5" />
                <span>New Form</span>
              </button>

              <button
                onClick={() => setActiveTab('my-applications')}
                className={`px-3 py-1.5 rounded-md transition-all flex items-center space-x-1.5 ${
                  activeTab === 'my-applications'
                    ? 'bg-white text-emerald-700 shadow-xs font-bold'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <Layers className="w-3.5 h-3.5" />
                <span>My Applications</span>
              </button>

              <button
                onClick={() => setActiveTab('officer-queue')}
                className={`px-3 py-1.5 rounded-md transition-all flex items-center space-x-1.5 ${
                  activeTab === 'officer-queue'
                    ? 'bg-emerald-700 text-white shadow-xs font-bold'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                <UserCheck className="w-3.5 h-3.5" />
                <span>Officer Queue</span>
              </button>
            </nav>

            {/* Persona Switcher Selector */}
            <div className="flex items-center space-x-2 bg-slate-50 border border-slate-300 rounded-lg px-2.5 py-1 text-xs">
              <span className="text-slate-500 font-medium whitespace-nowrap hidden md:inline">
                Active Persona:
              </span>
              <select
                value={currentPersona.id}
                onChange={handlePersonaChange}
                className="bg-transparent font-semibold text-slate-800 focus:outline-none cursor-pointer text-xs"
                title="Switch persona to test citizen applicant or officer roles across Dehradun and Haridwar"
              >
                <optgroup label="Dehradun Personas">
                  <option value="applicant-ddn">Applicant (Aarav - 9990000001)</option>
                  <option value="verifier-ddn">Junior Engineer (JE Verifier)</option>
                  <option value="approver-ddn">Executive Engineer (EE Approver)</option>
                </optgroup>
                <optgroup label="Haridwar Personas">
                  <option value="applicant-hdw">Applicant (Pooja - 9888000002)</option>
                  <option value="verifier-hdw">Junior Engineer (JE Haridwar)</option>
                  <option value="approver-hdw">Executive Engineer (EE Haridwar)</option>
                </optgroup>
              </select>
            </div>
          </div>

        </div>
      </div>
    </header>
  );
};
