import {
  ApplicationCreatePayload,
  ApplicationDetail,
  CalculationInput,
  CalculationResult,
  RequestInfo,
  SearchCriteria,
} from '../types';

const API_BASE = '/rcp/v1';

export class ApiServiceError extends Error {
  code: string;
  constructor(code: string, message: string) {
    super(message);
    this.code = code;
    this.name = 'ApiServiceError';
  }
}

async function handleResponse<T>(res: Response): Promise<T> {
  const json = await res.json().catch(() => null);
  if (!res.ok) {
    if (json && json.Errors && json.Errors.length > 0) {
      const err = json.Errors[0];
      throw new ApiServiceError(err.code || 'API_ERROR', err.message || 'Request failed');
    }
    throw new ApiServiceError('HTTP_' + res.status, `Request failed with status ${res.status}`);
  }
  return json as T;
}

export const api = {
  async calculateFee(calculation: CalculationInput, requestInfo: RequestInfo): Promise<CalculationResult> {
    const res = await fetch(`${API_BASE}/_calculate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        RequestInfo: requestInfo,
        Calculation: calculation,
      }),
    });
    const data = await handleResponse<{ Calculation: CalculationResult }>(res);
    return data.Calculation;
  },

  async createApplication(application: ApplicationCreatePayload, requestInfo: RequestInfo): Promise<ApplicationDetail> {
    const res = await fetch(`${API_BASE}/_create`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        RequestInfo: requestInfo,
        Application: application,
      }),
    });
    const data = await handleResponse<{ Application: ApplicationDetail }>(res);
    return data.Application;
  },

  async applyAction(
    applicationNumber: string,
    action: string,
    comment?: string,
    application?: ApplicationCreatePayload,
    requestInfo?: RequestInfo
  ): Promise<ApplicationDetail> {
    const res = await fetch(`${API_BASE}/_action`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        RequestInfo: requestInfo,
        Action: {
          applicationNumber,
          action,
          comment: comment || '',
        },
        Application: application,
      }),
    });
    const data = await handleResponse<{ Application: ApplicationDetail }>(res);
    return data.Application;
  },

  async searchApplications(criteria: SearchCriteria, requestInfo: RequestInfo): Promise<ApplicationDetail[]> {
    const res = await fetch(`${API_BASE}/_search`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        RequestInfo: requestInfo,
        SearchCriteria: criteria,
      }),
    });
    const data = await handleResponse<{ Applications: ApplicationDetail[] }>(res);
    return data.Applications || [];
  },
};
