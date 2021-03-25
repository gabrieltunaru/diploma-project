import express from 'express'
import User from '../models/user'
import bcrypt from 'bcrypt'
const router = express.Router()

async function login(req, res, dbUser) {
  const reqUser = req.body
  bcrypt
    .compare(reqUser.password, dbUser.password)
    .then((equal) => {
      if (equal) {
        const user = dbUser
        const token = user.generateAuthToken()
        res.header('x-auth-token', token).json(token)
      } else {
        res.status(400).send('Wrong password')
      }
    })
    .catch((err) => {
      console.error(err)
      res.status(500).send(err)
    })
}

async function register(req, res) {
  const user = new User({
    password: req.body.password,
    email: req.body.email,
  })
  user.password = await bcrypt.hash(user.password, 10)
  await user.save()
  const token = user.generateAuthToken()
  res.header('x-auth-token', token).json(token)
}

router.post('login', async (req, res) => {
  try {
    const user = await User.findOne({ email: req.body.email })
    if (user) {
      login(req, res, user)
    } else {
      register(req, res)
    }
  } catch (err) {
    console.error(err)
    res.status(500).send(err)
  }
})
