import userModel from '../models/user'
import * as generalMid from './general'
import fs from 'fs'
import User from '../models/user'
import express from 'express'
import auth from "./auth"
import upload from "./upload"

const router = express.Router()

router.post('/setProfile', auth, async (req, res) => {
    try {
        console.log('got profile', req.body)
        const profile = req.body
        const userId = generalMid.decoded(req.headers)._id
        const user = await User.findById(userId)
        user.profile = profile
        await user.save()
        res.status(200).send({})
    } catch (err) {
        console.error(err)
        res.status(500).json(err)
    }
})

const getProfile = async (req, res) => {
    try {
        const userId = generalMid.decoded(req.headers)._id
        const user = await User.findById(userId).populate({
            path: 'profile',
            model: 'Profile',
        })
        res.json(user.profile)
    } catch (err) {
        console.error(err)
        res.status(500).send(err)
    }
}

router.post('/setPhoto', [auth, upload], async (req, res, next) => {
    try {
        const file = req.file
        if (!file) {
            res.status(400).send("Didn't receive a file")
        }
        const decoded = generalMid.decoded(req.headers)
        const user = (await userModel.findById(decoded._id))
        const profile = user.profile
        profile.photo = file.filename
        await user.save()
        res.send(file.filename)
    } catch (err) {
        console.error(err)
        res.status(400).send(err)
    }
})

router.get('/image/:filename', async (req, res) => {
    const {filename} = req.params
    fs.readFile(generalMid.getFile(filename), (err, data) => {
        if (err) {
            console.error(err)
            res.sendStatus(400)
        } else {
            res.send(data)
        }
    })
})

export {
    getProfile,
}
export default router
