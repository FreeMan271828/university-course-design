import { setupWorker } from 'msw/browser'
import { handlers } from './handlers'

const worker = setupWorker(...handlers)

export function startMock() {
  return worker.start({
    onUnhandledRequest: 'bypass',
  })
}

