import React, { createContext, useContext, useState, useEffect } from 'react';
import { Persona, RequestInfo } from '../types';

export const PERSONAS: Persona[] = [
  {
    id: 'applicant-ddn',
    label: 'Aarav (Applicant - Dehradun)',
    roleCode: 'APPLICANT',
    roleName: 'Citizen Applicant',
    tenantId: 'dehradun',
    tenantName: 'Dehradun Municipal Corp',
    userName: '9990000001',
    name: 'Aarav Sharma',
    mobile: '9990000001',
  },
  {
    id: 'verifier-ddn',
    label: 'Vikram (JE / Verifier - Dehradun)',
    roleCode: 'VERIFIER',
    roleName: 'Junior Engineer',
    tenantId: 'dehradun',
    tenantName: 'Dehradun Municipal Corp',
    userName: 'je_dehradun',
    name: 'Vikram Negi (JE)',
    mobile: '9876543210',
  },
  {
    id: 'approver-ddn',
    label: 'Suresh (EE / Approver - Dehradun)',
    roleCode: 'APPROVER',
    roleName: 'Executive Engineer',
    tenantId: 'dehradun',
    tenantName: 'Dehradun Municipal Corp',
    userName: 'ee_dehradun',
    name: 'Suresh Rawat (EE)',
    mobile: '9876543211',
  },
  {
    id: 'applicant-hdw',
    label: 'Pooja (Applicant - Haridwar)',
    roleCode: 'APPLICANT',
    roleName: 'Citizen Applicant',
    tenantId: 'haridwar',
    tenantName: 'Haridwar Municipal Corp',
    userName: '9888000002',
    name: 'Pooja Verma',
    mobile: '9888000002',
  },
  {
    id: 'verifier-hdw',
    label: 'Amit (JE / Verifier - Haridwar)',
    roleCode: 'VERIFIER',
    roleName: 'Junior Engineer',
    tenantId: 'haridwar',
    tenantName: 'Haridwar Municipal Corp',
    userName: 'je_haridwar',
    name: 'Amit Joshi (JE)',
    mobile: '9876543220',
  },
  {
    id: 'approver-hdw',
    label: 'Rajendra (EE / Approver - Haridwar)',
    roleCode: 'APPROVER',
    roleName: 'Executive Engineer',
    tenantId: 'haridwar',
    tenantName: 'Haridwar Municipal Corp',
    userName: 'ee_haridwar',
    name: 'Rajendra Pant (EE)',
    mobile: '9876543221',
  },
];

interface AuthContextType {
  currentPersona: Persona;
  setPersona: (persona: Persona) => void;
  getRequestInfo: () => RequestInfo;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentPersona, setCurrentPersona] = useState<Persona>(() => {
    const saved = localStorage.getItem('rcp_active_persona');
    if (saved) {
      const found = PERSONAS.find(p => p.id === saved);
      if (found) return found;
    }
    return PERSONAS[0];
  });

  useEffect(() => {
    localStorage.setItem('rcp_active_persona', currentPersona.id);
  }, [currentPersona]);

  const getRequestInfo = (): RequestInfo => {
    return {
      apiId: 'portal',
      ver: '1.0',
      ts: Date.now(),
      msgId: `${Date.now()}|en_IN`,
      userInfo: {
        uuid: `u-${currentPersona.userName}`,
        userName: currentPersona.userName,
        name: currentPersona.name,
        mobileNumber: currentPersona.mobile,
        tenantId: currentPersona.tenantId,
        roles: [{ code: currentPersona.roleCode, name: currentPersona.roleName }],
      },
    };
  };

  return (
    <AuthContext.Provider value={{ currentPersona, setPersona: setCurrentPersona, getRequestInfo }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
