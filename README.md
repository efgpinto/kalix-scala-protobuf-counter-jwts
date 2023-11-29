# Implementing a Counter to Exemplify JWT usage

> **NOTE**: this repo contains a Kalix sample application written using the Scala Protobuf SDK and showcasing the use of JWTs. If not aware of Kalix, please visit the [Kalix docs](https://docs.kalix.io) first.

## Running Locally

To start your service locally, run:

```shell
sbt runAll
```

## Exercise the service

Increase endpoint requires `ten` claim to be provided and a role "role1", JWT claims used below: 
```json
{
  "iss": "my-issuer",
  "ten": "east",
  "roles": "role1",
  "sub": "hello1"
}
```

```shell
grpcurl --plaintext -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteS1pc3N1ZXIiLCJ0ZW4iOiJlYXN0Iiwic3ViIjoiaGVsbG8xIiwicm9sZXMiOiJyb2xlMSJ9' -d '{"counter_id": "1", "value": 10}' localhost:9000 com.example.CounterService/Increase
```

> **NOTE**: we don't provide the tenant id on the body, but it still gets extract to the command to be part of the composite entity key.

Get endpoint requires `ten` to be provided but no role:
```json
{
  "iss": "my-issuer",
  "ten": "east"
}
```

```shell
grpcurl -v --plaintext -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteS1pc3N1ZXIiLCJ0ZW4iOiJlYXN0In0' -d '{"counter_id": "1", "tenant_id":"east"}' localhost:9000 com.example.CounterService/GetCurrentCounter
```

Increasing without the `sub` claim, still works:
```json
{
  "iss": "my-issuer",
  "ten": "east",
  "roles": "role1"
}
```

```shell
grpcurl -v --plaintext -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteS1pc3N1ZXIiLCJ0ZW4iOiJlYXN0Iiwicm9sZXMiOiJyb2xlMSJ9' -d '{"counter_id": "1", "value": 10}' localhost:9000 com.example.CounterService/Increase
```

Decreasing required "role2" instead of "role1":
```json
{
  "iss": "my-issuer",
  "ten": "east",
  "roles": "role2"
}
```

```shell
grpcurl --plaintext -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteS1pc3N1ZXIiLCJ0ZW4iOiJlYXN0Iiwicm9sZXMiOiJyb2xlMiJ9' -d '{"counter_id": "1", "value": 10}' localhost:9000 com.example.CounterService/Decrease
```

### Authentication failure examples

Missing tenant in JWT, fails:
```shell
grpcurl -v --plaintext -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteS1pc3N1ZXIifQ' -d '{"counter_id": "1", "value": 10}' localhost:9000 com.example.CounterService/Increase
```


Decreasing with role1 instead of role2, fails:
```shell
grpcurl --plaintext -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteS1pc3N1ZXIiLCJ0ZW4iOiJlYXN0Iiwic3ViIjoiaGVsbG8xIiwicm9sZXMiOiJyb2xlMSJ9' -d '{"counter_id": "1", "value": 10}' localhost:9000 com.example.CounterService/Decrease
```