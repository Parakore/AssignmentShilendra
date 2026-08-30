import React from 'react';
import { ActionHistory } from '../types';

interface StatusTimelineProps {
  timeline: ActionHistory[];
}

export const StatusTimeline: React.FC<StatusTimelineProps> = ({ timeline }) => {
  if (!timeline || timeline.length === 0) {
    return (
      <div className="text-sm text-slate-500 italic py-3 text-center bg-slate-50 rounded-lg border border-slate-200">
        No transition history recorded yet.
      </div>
    );
  }

  const formatTimestamp = (epochMillis: number) => {
    if (!epochMillis) return '';
    const date = new Date(epochMillis);
    return date.toLocaleString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const getActionColor = (action: string) => {
    switch (action.toUpperCase()) {
      case 'CREATE':
        return 'bg-blue-600 text-white';
      case 'VERIFY':
        return 'bg-indigo-600 text-white';
      case 'APPROVE':
        return 'bg-emerald-600 text-white';
      case 'REJECT':
        return 'bg-rose-600 text-white';
      case 'SEND_BACK':
        return 'bg-amber-600 text-white';
      case 'CANCEL':
        return 'bg-slate-600 text-white';
      case 'EDIT':
        return 'bg-cyan-600 text-white';
      default:
        return 'bg-slate-600 text-white';
    }
  };

  return (
    <div className="flow-root">
      <ul className="-mb-8">
        {timeline.map((item, idx) => {
          const isLast = idx === timeline.length - 1;
          return (
            <li key={item.id || idx}>
              <div className="relative pb-8">
                {!isLast && (
                  <span
                    className="absolute left-4 top-4 -ml-px h-full w-0.5 bg-slate-200"
                    aria-hidden="true"
                  />
                )}
                <div className="relative flex space-x-3 items-start">
                  <div>
                    <span
                      className={`h-8 w-8 rounded-full flex items-center justify-center ring-4 ring-white text-xs font-bold uppercase shadow-sm ${getActionColor(
                        item.action
                      )}`}
                    >
                      {item.action.substring(0, 2)}
                    </span>
                  </div>
                  <div className="flex min-w-0 flex-1 justify-between space-x-4 pt-1">
                    <div>
                      <div className="flex flex-wrap items-center gap-2">
                        <span className="text-sm font-semibold text-slate-900">
                          {item.action}
                        </span>
                        <span className="text-xs px-2 py-0.5 rounded bg-slate-100 text-slate-700 font-medium border border-slate-200">
                          {item.actorRole}
                        </span>
                        {item.fromStatus && (
                          <span className="text-xs text-slate-500">
                            ({item.fromStatus} → <span className="font-semibold text-slate-700">{item.toStatus}</span>)
                          </span>
                        )}
                        {!item.fromStatus && (
                          <span className="text-xs text-slate-500">
                            (State: <span className="font-semibold text-slate-700">{item.toStatus}</span>)
                          </span>
                        )}
                      </div>

                      <p className="text-xs text-slate-600 mt-1">
                        By <span className="font-medium text-slate-800">{item.actorName}</span>
                      </p>

                      {item.comment && (
                        <div className="mt-2 text-xs text-slate-700 bg-slate-50 border border-slate-200 rounded-md p-2.5 shadow-sm">
                          <span className="font-semibold text-slate-500 block mb-0.5">Remarks / Reason:</span>
                          {item.comment}
                        </div>
                      )}
                    </div>
                    <div className="whitespace-nowrap text-right text-xs text-slate-400">
                      <time dateTime={new Date(item.createdTime).toISOString()}>
                        {formatTimestamp(item.createdTime)}
                      </time>
                    </div>
                  </div>
                </div>
              </div>
            </li>
          );
        })}
      </ul>
    </div>
  );
};
