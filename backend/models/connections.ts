import mongoose, {Document, Schema, SchemaDefinition} from 'mongoose'
import {IUser} from './user'

export interface IConnection extends Document {
  otherUser?: IUser
  isPrivate: boolean,
  users: [IUser]
}

const ConnectionSchema: Schema<IConnection> = new Schema<IConnection>(
  {
    isPrivate: {
      type: Boolean,
      default: false
    },
    users: [{
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    }]
  }
)

const ConnectionModel = mongoose.model<IConnection>('Connection', ConnectionSchema)

export default ConnectionModel
