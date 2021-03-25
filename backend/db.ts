import mongoose from 'mongoose'
function init() {
  mongoose
    .connect('mongodb://172.17.0.1/diploma', {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    })
    .then(() => {
      console.log('Connected to MongoDB')
    })
    .catch((e) => console.error(e))
}

const db = { init }

export default db
