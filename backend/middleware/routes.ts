import express from 'express'
import userMid from './user'
import profileMid from './profile'
import generalMid from './general'
import contactsMid from './contacts'
import messagesMid from './messages'
const router = express.Router()
router.use('/user', userMid)
router.use('/profile', profileMid)
router.use('/general', generalMid)
router.use('/contacts', contactsMid)
router.use('/messages', messagesMid)
export default router
