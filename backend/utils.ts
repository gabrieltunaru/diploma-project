import mongoose from "mongoose"
import logger from './logger'

function getErrorMessage(err) {
  let errMessage = ''
  if (err instanceof mongoose.Error.ValidationError) {
    for (const field of Object.keys(err.errors)) {
      const e = err.errors[field]
      if (e instanceof mongoose.Error.ValidatorError) {
        errMessage+= e.properties.message
      } else {
        errMessage += e.reason
      }
    }
  }
  if (!errMessage) {
    errMessage = 'Error'
  }
  return errMessage
}

async function errorHandler(err, req, res, next) {
  console.error(err)
  logger.debug('mid')
  const errMessage = getErrorMessage(err)
  res.status(400).send(errMessage)
}

const asyncHandler = fn => (req, res, next) => {
  return Promise
    .resolve(fn(req, res, next))
    .catch(next);
};

export {getErrorMessage, errorHandler, asyncHandler}
