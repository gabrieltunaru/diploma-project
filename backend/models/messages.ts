import mongoose, {Document, Schema} from 'mongoose'

export interface IMessage extends Document {
  type: string,
  senderId: string,
  text: string,
  otherUserId: string,
  conversationId: string,
  timestamp: string
}

const MessageSchema: Schema<IMessage> = new Schema<IMessage>(
  {
    type: String,
    senderId: {type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    text: String,
    otherUserId: {type: mongoose.Schema.Types.ObjectId, ref: 'User'},
    conversationId: String,
    timestamp: String
  }
)

const MessageModel = mongoose.model<IMessage>('Message', MessageSchema)

export default MessageModel
