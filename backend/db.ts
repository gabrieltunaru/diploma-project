import mongoose from 'mongoose'
import logger from './logger'
function init() {
  mongoose
    .connect('mongodb://172.17.0.1/diploma', {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    })
    .then(() => {
      logger.info('Connected to MongoDB')
    })
    .catch((e) => console.error(e))
}

const db = { init }

export default db
