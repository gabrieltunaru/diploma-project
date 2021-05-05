import config from 'config'
import jwt from 'jsonwebtoken'
import mongoose, {Document, Schema} from 'mongoose'
import {IConversation} from './conversations'

interface IProfile extends Document {
  username: string
  details: string
  photo: string
}
export interface IUser extends Document {
  email: string
  password: string
  profile: IProfile,
  conversations: [IConversation],
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
  profile: {
    username: {
      type: String,
    },
    details: {
      type: String,
    },
    photo: {
      type: String,
    },
  },
  conversations: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Conversation',
  }]
})

UserSchema.methods.generateAuthToken = function () {
  return jwt.sign(
    {
      _id: this._id,
      email: this.email,
    },
    config.get('privateKey')
  )
}

const User = mongoose.model<IUser>('User', UserSchema)

export default User
