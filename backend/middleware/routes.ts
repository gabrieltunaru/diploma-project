import express from 'express'
import userMid from './user'
const router = express.Router()
router.use('/user', userMid)
export default router
