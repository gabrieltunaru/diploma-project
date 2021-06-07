import nodemailer from 'nodemailer'
import config from 'config'

const user = config.get('email')
const pass = config.get('pass')

const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {user, pass}
})

function sendMail(email, token) {
  const mailOptions = {
    from: 'Messaging application',
    to: email,
    subject: 'Activate your account',
    text: `Confirm your email by accessing http://localhost:3000/user/activate/${token}`
  }

  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      console.log(error)
    } else {
      console.log('Email sent: ' + info.response)
    }
  })
}

export default sendMail
