# Bale

A simple API for the Minecraft server made for HawksBill bot.
Includes support for cross-messaging, rcon, scarpet, and more.

## Technical Details


### Encryption

All data payloads must be encrypted with AES in GCM mode with No padding. AES keys are 256-bit and are provided by generated
by the server. 

Initialization Vector (IV) is 16 Bytes

To generate a key, use `/bale generate`


### Protocol

Heading are in format of `Name [size, explanation]`

#### Marker [1 byte, Must be 0x45]

First byte must be `0x45` to indicate the connection packet sent is a bale packet and should be intercepted

#### Packet Type [1 byte, Index in PacketType enum (Starts at 0)]

Type of packet to be process
 
#### Payload length [2 bytes, Max Length: 65535]

Length of the payload 

#### Payload [^, IV + Encrypted Payload]

Encrypted payload of size specified in payload length. The First 16 bytes must be IV and rest should be the payload itself.
```
