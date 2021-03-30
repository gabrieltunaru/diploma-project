import config from 'config'
import jwt from 'jsonwebtoken'
import Joi from 'joi'
import mongoose, { Schema, Document } from 'mongoose'

export interface IUser extends Document {
  email: string
  password: string
  generateAuthToken: () => string
}

const UserSchema: Schema<IUser> = new mongoose.Schema<IUser>({
  email: {
    type: String,
    required: true,
    minlength: 5,
    maxlength: 255,
    unique: true,
  },
  password: {
    type: String,
    required: true,
    minlength: 3,
    maxlength: 255,
    unique: true,
  },
})

UserSchema.methods.generateAuthToken = function () {
  const token = jwt.sign(
    {
      _id: this._id,
      email: this.email,
    },
    config.get('privateKey')
  )
  return token
}

const User = mongoose.model<IUser>('User', UserSchema)

export default User