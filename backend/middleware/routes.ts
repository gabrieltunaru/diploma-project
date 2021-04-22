import express from 'express'
import userMid from './user'
import profileMid from './profile'
import generalMid from './general'
import contactsMid from './contacts'
const router = express.Router()
router.use('/user', userMid)
router.use('/profile', profileMid)
router.use('/general', generalMid)
router.use('/contacts', contactsMid)
export default router
