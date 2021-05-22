import mongoose from 'mongoose'
import logger from './logger'

const MONGO_URL = 'mongodb://172.17.0.1/diploma'
function init() {
  mongoose
    .connect(MONGO_URL, {
      useNewUrlParser: true,
    })
    .then(() => {
      logger.info('Connected to MongoDB')
    })
    .catch((e) => logger.error(e))
}

const db = { init }

export default db
