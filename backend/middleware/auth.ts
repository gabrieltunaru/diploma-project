import jwt from 'jsonwebtoken'
import config from 'config'
import {getUserFromToken} from './general'

export default async function (req, res, next) {
    const token = req.headers['x-auth-token'] || req.headers.authorization
    if (!token) return res.status(401).send('Access denied. No token provided.')
    try {
        req.user = jwt.verify(token, config.get('privateKey'))
        const user = await getUserFromToken(token)
        if (user.isActivated) {
            next()
        } else {
            res.sendStatus(401)
        }
    } catch (ex) {
        res.status(400).send('Invalid token')
    }
}
