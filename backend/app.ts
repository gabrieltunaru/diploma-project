import express from 'express'
import bodyParser from 'body-parser'

const app = express()
app.use(bodyParser.json())

const port = 3000

app.post('/', (req, res) => {
  console.log(req.body)
  const x={resu: 'Eureka'}
  res.send(x)
})

app.listen(port, ()=> {
   console.log(`server is listening on ${port}`)
})
