import upload from "./upload";

import express from "express";
import path from "path";
import config from "config";
import jwt from "jsonwebtoken";
import User from '../models/user'

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
    return User.findById(userId)
}

const getUserIdFromTokenString = token => {
    const decodedUser = jwt.verify(token, config.get('privateKey'))
    return decodedUser._id
}

function getFile(filename) {
    return __dirname + '/../assets/images/' + filename
}

function addOne(schema) {
    return async (req, res) => {
        try {
            console.log(req.body)
            const model = new schema(req.body)
            await model.save()
            res.sendStatus(201)
        } catch (err) {
            res.status(400).send(err)
        }
    }
}

const getImage = async (req, res) => {
    try {
        const {filename} = req.params
        console.log(filename)
        // fs.readFile(getFile(filename), function (err, data) {
        //   if (err) throw err
        //   res.send(data)
        // })
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
    addOne,
    getImage,
    uploadPhoto,
    getUserId,
    getUserIdFromTokenString,
    getUser
}

export default router
