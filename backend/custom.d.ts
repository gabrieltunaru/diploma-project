import { IDecodedUser } from './models/userModel'

declare global {
  namespace Express {
    interface Request {
      user: IDecodedUser,
      file: any
    }
  }
}
