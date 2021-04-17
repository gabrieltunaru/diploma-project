import express from 'express'
import User, {IDecodedUser} from '../models/user'
import bcrypt from 'bcrypt'
import auth from './auth'
import {ProfileModel} from '../models/profileModel'

const router = express.Router()

interface AuthResponse {
    key: string
    notFound: boolean
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
                const user = dbUser
                const token = user.generateAuthToken()
                const resp: AuthResponse = {notFound: false, key: token}
                console.log(resp)
                res.header('x-auth-token', token).json(resp)
            } else {
                res.status(400).send('Wrong password')
            }
        })
        .catch((err) => {
            console.error(err)
            console.error(req.body)
            res.status(500).send(err)
        })
}

async function sendNotFound(req, res) {
    const resp: AuthResponse = {notFound: true, key: null}
    res.json(resp)
}

router.post('/auth', async (req, res) => {
    try {
        const user = await User.findOne({email: req.body.email})
        if (user) {
            await login(req, res, user)
        } else {
            await sendNotFound(req, res)
        }
    } catch (err) {
        console.error(err)
        res.status(500).send(err)
    }
})

router.get('/getCurrent', auth, async (req, res) => {
    const user = await User.findById(req.user._id).select('-password').populate({
        path: 'profile',
        model: 'Profile',
    })
    console.log(user)
    res.send(user)
})

router.post('/register', async (req, res) => {
    const profile = await new ProfileModel().save()
    const user = new User({
        password: req.body.password,
        email: req.body.email,
        profile
    })
    user.password = await bcrypt.hash(user.password, 10)
    await user.save()
    const token = user.generateAuthToken()
    res.header('x-auth-token', token).json({key: token})
})

export default router
