import WebSocket from 'ws'
import logger from './logger'

function init(wss: WebSocket) {

  wss.on('connection', function connection(ws) {
    logger.debug('connection lel')

    ws.on('message', function incoming(message) {
      logger.debug(`received: ${message}`)
    })

    ws.send('something')
  })

}

export default {init}
