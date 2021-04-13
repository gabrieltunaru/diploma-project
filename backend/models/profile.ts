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

const Profile = mongoose.model<IProfile>('Profile', ProfileSchema)

export { Profile, ProfileSchema, IProfile }
