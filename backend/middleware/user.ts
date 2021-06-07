import express from 'express'
import UserModel, {IDecodedUser, IUser} from '../models/userModel'
import bcrypt from 'bcrypt'
import auth from './auth'
import {getUser} from './general'
import logger from '../logger'
import mongoose from 'mongoose'
import {asyncHandler, getErrorMessage} from '../utils'
import sendMail from '../mail'

const router = express.Router()

interface AuthResponse {
    key: string
}

declare global {
    namespace Express {
        interface Request {
            user: IDecodedUser
        }
    }
}


async function login(req, res, dbUser) {
    const reqUser = req.body
    bcrypt
        .compare(reqUser.password, dbUser.password)
        .then((equal) => {
            if (equal) {
                const token = dbUser.generateAuthToken()
                const resp: AuthResponse = {key: token}
                res.header('x-auth-token', token).json(resp)
            } else {
                res.status(400).send('Wrong password')
            }
        })
        .catch((err) => {
            res.status(400).send(err)
        })
}

router.post('/auth', async (req, res) => {
    const user = await UserModel.findOne({email: req.body.email})
    if (user) {
        await login(req, res, user)
    } else {
        res.sendStatus(404)
    }
})

router.get('/getCurrent', auth, async (req, res) => {
  const user = await UserModel.findById(req.user._id).select('-password').populate({
    path: 'profile',
    model: 'Profile',
  })
  console.log(user)
  res.send(user)
})


router.get('/activate/:token', async (req, res) => {
  const user: IUser = await UserModel.findOne({activationToken: req.params.token})
  if (!user) {
    res.sendStatus(404)
  } else {
    user.activationToken = ''
    user.isActivated = true
    await user.save()
    res.sendStatus(200)
  }
})

function randomString(length): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    let res = ''
    for (let i=0; i<length; i++) {
        res += chars[Math.floor(Math.random() * chars.length)]
    }
    return res
}


async function createPrivateId(): Promise<string> {
    const ids = await UserModel.find({}).select('privateId')
    let privateId = randomString(6)
    while (privateId in ids) {
        privateId = randomString(6)
    }
    return privateId
}

router.put('/privateId', auth, async (req, res) => {
    const user = await getUser(req.headers)
    user.privateId = await createPrivateId()
    await user.save()
    res.json({privateId: user.privateId})
})

router.post('/register', asyncHandler(async (req, res) => {
    const {pbKey, email, password} = req.body
    const privateId = await createPrivateId()
    const hashedPassword = await bcrypt.hash(password, 10)
    const activationToken = randomString(32)
    const user = new UserModel({
          password: hashedPassword,
          email,
          pbKey,
          privateId,
          profile: {displayName: 'User'},
          contacts: [],
          isActivated: false,
          activationToken
      })
      await user.save()
      sendMail(email, activationToken)
      const token = user.generateAuthToken()
      res.header('x-auth-token', token).json({key: token})
}))

export default router
