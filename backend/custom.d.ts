import { IDecodedUser } from './models/user'

declare global {
  namespace Express {
    interface Request {
      user: IDecodedUser
    }
  }
}
