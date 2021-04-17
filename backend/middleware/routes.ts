import express from 'express'
import userMid from './user'
import profileMid from './profile'
import generalMid from './general'
const router = express.Router()
router.use('/user', userMid)
router.use('/profile', profileMid)
router.use('/general', generalMid)
export default router
