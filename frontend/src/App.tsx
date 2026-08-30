import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Header } from './components/Header';
import { ApplicantApplyPage } from './pages/ApplicantApplyPage';
import { ApplicantApplicationsPage } from './pages/ApplicantApplicationsPage';
import { ApplicationDetailPage } from './pages/ApplicationDetailPage';
import { OfficerQueuePage } from './pages/OfficerQueuePage';
import { ApplicationDetail } from './types';

const MainApp: React.FC = () => {
  const { currentPersona } = useAuth();

  const [activeTab, setActiveTab] = useState<'apply' | 'my-applications' | 'officer-queue'>('apply');
  const [selectedApplication, setSelectedApplication] = useState<ApplicationDetail | null>(null);
  const [editingApplication, setEditingApplication] = useState<ApplicationDetail | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 4000);
  };

  const handleApplicationCreated = (app: ApplicationDetail) => {
    setEditingApplication(null);
    setSelectedApplication(app);
    showToast(`Application ${app.applicationNumber} successfully submitted!`);
  };

  const handleApplicationUpdated = (app: ApplicationDetail) => {
    setSelectedApplication(app);
    showToast(`Application ${app.applicationNumber} updated to status: ${app.status}`);
  };

  const handleEditRequested = (app: ApplicationDetail) => {
    setEditingApplication(app);
    setSelectedApplication(null);
    setActiveTab('apply');
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col font-sans">
      <Header
        activeTab={activeTab}
        setActiveTab={(tab) => {
          setSelectedApplication(null);
          setEditingApplication(null);
          setActiveTab(tab);
        }}
      />

      {/* Toast Banner */}
      {toastMessage && (
        <div className="fixed bottom-4 right-4 z-50 bg-slate-900 text-white text-xs font-semibold px-4 py-3 rounded-xl shadow-xl border border-slate-700 flex items-center space-x-2 animate-bounce">
          <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Main Content Area */}
      <main className="flex-1">
        {selectedApplication ? (
          <ApplicationDetailPage
            application={selectedApplication}
            onBack={() => setSelectedApplication(null)}
            onApplicationUpdated={handleApplicationUpdated}
            onEditRequested={handleEditRequested}
          />
        ) : activeTab === 'apply' ? (
          <ApplicantApplyPage
            onApplicationCreated={handleApplicationCreated}
            editingApplication={editingApplication}
            onCancelEdit={() => setEditingApplication(null)}
          />
        ) : activeTab === 'my-applications' ? (
          <ApplicantApplicationsPage
            onSelectApplication={(app) => setSelectedApplication(app)}
            onNewApplication={() => {
              setEditingApplication(null);
              setActiveTab('apply');
            }}
          />
        ) : (
          <OfficerQueuePage
            onSelectApplication={(app) => setSelectedApplication(app)}
          />
        )}
      </main>

      <footer className="bg-white border-t border-slate-200 py-4 text-center text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-2">
          <span>Municipal Corporation Road Cutting Permission Portal • Spec revision: 3.1-KESTREL</span>
          <span className="font-mono text-[11px] text-slate-400">Addendum 3.1 reviewRef: K7Q2</span>
        </div>
      </footer>
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
};

export default App;
