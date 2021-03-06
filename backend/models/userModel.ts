import config from 'config'
import jwt from 'jsonwebtoken'
import mongoose, {Document, Schema} from 'mongoose'
import {IConversation} from './conversations'

interface IProfile extends Document {
  displayName: string
  details: string
  photo: string
}

export interface IUser extends Document {
  email: string
  password: string
  profile: IProfile,
  conversations: [IConversation],
  pbKey: string,
  privateId: string,
  generateAuthToken: () => string
}

export interface IDecodedUser {
  _id: string
  email: string
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
  pbKey: String,
  privateId: {
    type: String,
    unique: true
  },
  profile: {
    displayName: String,
    username: {type: String, unique: true},
    details: String,
    photo: String,
  },
  conversations: {
    type: [{
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Conversation',
    }],
    default: []
  },
})

UserSchema.path('email').validate(email => {
  const emailRegex = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/
  return emailRegex.test(email)
}, 'The provided email is not valid.')

UserSchema.methods.generateAuthToken = function () {
  return jwt.sign(
    {
      _id: this._id,
      email: this.email,
    },
    config.get('privateKey')
  )
}

const UserModel = mongoose.model<IUser>('User', UserSchema)

export default UserModel
