import mongoose, {Document, Schema, SchemaDefinition} from 'mongoose'
import {IUser} from './user'

export interface IConversation extends Document {
  otherUser?: IUser
  isPrivate: boolean,
  users: [IUser]
}

const ConversationSchema: Schema<IConversation> = new Schema<IConversation>(
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

const ConversationModel = mongoose.model<IConversation>('Conversation', ConversationSchema)

export default ConversationModel
