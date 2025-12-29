type FilterOptions = {
  ticketIds: Array<number | string>
  actions: string[]
  operatorNames: string[]
}

const STORAGE_KEY = 'ticketLogFilterOptions_v1'

export function loadFilterOptions(): FilterOptions {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return { ticketIds: [], actions: [], operatorNames: [] }
    return JSON.parse(raw) as FilterOptions
  } catch (e) {
    console.error('loadFilterOptions parse error', e)
    return { ticketIds: [], actions: [], operatorNames: [] }
  }
}

export function saveFilterOptions(opts: Partial<FilterOptions>) {
  const cur = loadFilterOptions()
  const merged: FilterOptions = {
    ticketIds: Array.from(new Set([...(cur.ticketIds || []), ...(opts.ticketIds || [])])),
    actions: Array.from(new Set([...(cur.actions || []), ...(opts.actions || [])])),
    operatorNames: Array.from(new Set([...(cur.operatorNames || []), ...(opts.operatorNames || [])])),
  }
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(merged))
  } catch (e) {
    console.error('saveFilterOptions error', e)
  }
  return merged
}

export function clearFilterOptions() {
  localStorage.removeItem(STORAGE_KEY)
}


