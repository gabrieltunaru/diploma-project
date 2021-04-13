import express from 'express'
import userMid from './user'
import profileMid from './profile'
const router = express.Router()
router.use('/user', userMid)
router.use('/profile', profileMid)
export default router
