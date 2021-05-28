import jwt from 'jsonwebtoken'
import config from 'config'

export default function (req, res, next) {
    const token = req.headers['x-auth-token'] || req.headers.authorization
    if (!token) return res.status(401).send('Access denied. No token provided.')
    try {
        req.user = jwt.verify(token, config.get('privateKey'))
        next()
    } catch (ex) {
        res.status(400).send('Invalid token')
    }
}
