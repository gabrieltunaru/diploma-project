import mongoose, { Document } from 'mongoose'

interface IProfile extends Document {
  username: string
  details: string
  photo: string
}

const ProfileSchema = new mongoose.Schema({
  username: {
    type: String,
  },
  details: {
    type: String,
  },
  photo: {
    type: String,
  },
})

const ProfileModel = mongoose.model<IProfile>('Profile', ProfileSchema)

export { ProfileModel, ProfileSchema, IProfile }
