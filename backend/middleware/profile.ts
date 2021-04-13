import userModel from '../models/user'
import * as generalMid from './general'
import fs from 'fs'
import User from '../models/user'
import {Profile} from "../models/profile";
import express from 'express'
import auth from "./auth";

const router = express.Router()

router.post('/setProfile', auth, async (req, res) => {
    try {
        console.log('got profile', req.body)
        const profile = new Profile({...req.body})
        const userId = generalMid.decoded(req.headers)._id
        const user = await User.findById(userId)
        user.profile = profile
        await profile.save()
        await user.save()
        res.status(200).send({})
    } catch (err) {
        console.error(err)
        res.status(500).json(err)
    }
})

const update = async (req, res) => {
    try {
        const profile = await Profile.findByIdAndUpdate(req.body._id, req.body)
        const userId = generalMid.decoded(req.headers)._id
        const user = await User.findById(userId)
        user.profile = profile
        await user.save()
        res.status(200).send({})
    } catch (err) {
        console.error(err)
        res.status(500).json(err)
    }
}

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

const uploadPhoto = async (req, res, next) => {
    try {
        const modelName = `profile.photo`
        const file = req.file
        if (!file) {
            res.status(400).send("Didn't receive a file")
        }
        const decoded = generalMid.decoded(req.headers)
        await userModel.update(
            {_id: decoded._id},
            {[modelName]: file.filename}
        )
        res.send(file)
    } catch (err) {
        res.status(400).send(err)
    }
}

const getImage = async (req, res) => {
    const {filename} = req.params
    fs.readFile(generalMid.getFile(filename), (err, data) => {
        if (err) throw err
        res.send(data)
    })
}

export {
    getProfile,
    uploadPhoto,
    getImage,
    update,
}
export default router
