import express from "express"
import auth from './auth'
import {asyncHandler} from '../utils'
import {getUserId, getUserIdFromTokenString} from './general'
import MessageModel from '../models/messages'

const router = express.Router()

router.get('/', [auth], asyncHandler(async (req, res, next) => {
  const userId = getUserId(req.headers)
  const messages = await MessageModel.find({otherUserId: userId})
  res.json({messages})
  await MessageModel.deleteMany({otherUserId: userId})
}))

export default router
