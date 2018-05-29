package application

import org.springframework.cloud.contract.spec.Contract

Contract.make {
  request {
    method 'DELETE'
    url '/api/apps'
    headers {
      contentType(applicationJson())
    }
  }
  response {
    status 200
  }
}