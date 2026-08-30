export interface RoleInfo {
  code: string;
  name?: string;
  tenantId?: string;
}

export interface UserInfo {
  uuid: string;
  userName: string;
  name: string;
  mobileNumber?: string;
  emailId?: string;
  tenantId: string;
  roles: RoleInfo[];
}

export interface RequestInfo {
  apiId: string;
  ver?: string;
  ts?: number;
  action?: string;
  did?: string;
  key?: string;
  msgId: string;
  authToken?: string;
  userInfo: UserInfo;
}

export interface ResponseInfo {
  apiId?: string;
  ver?: string;
  ts?: number;
  resMsgId?: string;
  msgId?: string;
  status: 'successful' | 'failed';
}

export interface ApiError {
  code: string;
  message: string;
  description?: string;
}

export interface CalculationInput {
  tenantId: string;
  roadType: string;
  lengthInMeters: number;
  widthInMeters: number;
  durationInDays: number;
  applicantType: string;
  proposedStartDate: string;
  applicationDate?: string;
}

export interface CalculationResult {
  areaInSqm: number;
  restorationCharge: number;
  permissionFee: number;
  urgencySurcharge: number;
  securityDeposit: number;
  totalAmount: number;
  reviewRef: string;
  breakdownDetails?: {
    rawProductArea?: string;
    roundedAreaSqm?: number;
    roadTypeName?: string;
    restorationRatePerSqm?: number;
    permissionRatePerSqmPerDay?: number;
    minSecurityDepositFloor?: number;
    securityDepositPercent?: number;
    calculatedDepositFromPercent?: number;
    daysUntilStart?: number;
    urgencyThresholdDays?: number;
    isUrgent?: boolean;
    urgencySurchargePercent?: number;
    isGovtAgency?: boolean;
  };
}

export interface ActionHistory {
  id: string;
  action: string;
  fromStatus: string | null;
  toStatus: string;
  actorUuid: string;
  actorName: string;
  actorRole: string;
  comment?: string;
  createdTime: number;
}

export interface ApplicationDetail {
  id: string;
  applicationNumber: string;
  tenantId: string;
  status: 'APPLIED' | 'PENDING_APPROVAL' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
  applicantName: string;
  applicantMobile: string;
  applicantEmail?: string;
  applicantType: string;
  roadType: string;
  roadTypeName?: string;
  lengthInMeters: number;
  widthInMeters: number;
  areaInSqm: number;
  durationInDays: number;
  proposedStartDate: string;
  applicationDate: string;
  location: string;
  description?: string;
  calculation: CalculationResult;
  timeline: ActionHistory[];
  allowedActions: string[];
  createdTime: number;
  createdBy: string;
  lastModifiedTime: number;
  lastModifiedBy: string;
}

export interface ApplicationCreatePayload {
  tenantId: string;
  roadType: string;
  lengthInMeters: number;
  widthInMeters: number;
  durationInDays: number;
  applicantType: string;
  proposedStartDate: string;
  applicantName: string;
  applicantMobile: string;
  applicantEmail?: string;
  location: string;
  description?: string;
}

export interface SearchCriteria {
  applicationNumber?: string;
  status?: string;
  mobileNumber?: string;
  tenantId?: string;
  offset?: number;
  limit?: number;
}

export interface Persona {
  id: string;
  label: string;
  roleCode: 'APPLICANT' | 'VERIFIER' | 'APPROVER';
  roleName: string;
  tenantId: string;
  tenantName: string;
  userName: string;
  name: string;
  mobile: string;
}
