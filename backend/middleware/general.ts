import upload from "./upload";

import express from "express";
import path from "path";
import config from "config";
import jwt from "jsonwebtoken";
import UserModel from '../models/userModel'

const router = express.Router()
const decoded = (headers) => {
    const token = headers['x-auth-token']
    return jwt.verify(token, config.get('privateKey'))
}

const getUserId = headers => {
    const user = decoded(headers)
    return user._id
}

const getUser = (headers) => {
    const userId = getUserId(headers)
    return UserModel.findById(userId)
}

const getUserIdFromTokenString = token => {
    const decodedUser = jwt.verify(token, config.get('privateKey'))
    return decodedUser._id
}

const getUserFromToken = async token => {
    const userId = getUserIdFromTokenString(token)
    return UserModel.findById(userId)
}

function getFile(filename) {
    return __dirname + '/../assets/images/' + filename
}

const getImage = async (req, res) => {
    try {
        const {filename} = req.params
        res.sendFile(path.resolve(getFile(filename)))
    } catch (err) {
        console.error(err)
        res.sendStatus(400)
    }
}

const uploadPhoto = async (req, res, next) => {
    try {
        const file = req.file
        if (!file) {
            res.status(400).send("Didn't receive a file")
        }
        res.send(file)
    } catch (err) {
        res.status(400).send(err)
    }
}

router.post('/image', upload, uploadPhoto)
router.get('/image/:filename', getImage)

export {
    decoded,
    getFile,
    getImage,
    uploadPhoto,
    getUserId,
    getUserIdFromTokenString,
    getUser,
    getUserFromToken
}

export default router
