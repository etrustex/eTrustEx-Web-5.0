import useProgressTrackerStore from '@/messages/store/progress'
import { createHash, Hash } from 'crypto'

/* the default digests from crypto transform a stream into the digest if used in a pipe */
export function createDigester(): Hash {
  return createHash('sha512')
    .setEncoding('hex')
}

export class FixedChunkSizeTransformStream extends TransformStream {
  constructor(buffer: Uint8Array, position = 0) {
    super({
      async transform(chunk: any, controller: any) {
        let offset = 0
        while (offset < chunk.length) {
          const spaceRemaining = buffer.length - position
          const copyLength = Math.min(spaceRemaining, chunk.length - offset)

          // Copy data from the incoming chunk to the buffer
          buffer.set(chunk.subarray(offset, offset + copyLength), position)
          position += copyLength
          offset += copyLength

          if (position === buffer.length) {
            // Buffer is full, process the complete chunk
            controller.enqueue(new Uint8Array(buffer))
            position = 0
          }
        }
      },

      flush(controller: any) {
        // Process any remaining data in the buffer
        if (position > 0) {
          // Modify this line to apply your transformations
          controller.enqueue(buffer.subarray(0, position))
        }
      },
    })
  }
}

export function trackingTransformStream() {
  const progressTracker = useProgressTrackerStore()
  return new TransformStream({
    transform(chunk, controller) {
      progressTracker.addBytes(chunk.length)
      controller.enqueue(chunk)
    },
  })
}

export function digesterTransformStream(digester: Hash, expectedMd?: string) {
  return new TransformStream({
    transform(chunk, controller) {
      digester.update(chunk)
      controller.enqueue(chunk)
    },
    flush(controller) {
      if (expectedMd) {
        const computedMd = digester.digest('hex')
        if (expectedMd.toLowerCase() != computedMd.toLowerCase()) {
          console.error(`Checksum mismatch: Expected ${ expectedMd }, computed ${ computedMd }`)
          // controller.error(new Error(`Checksum mismatch: Expected ${ md }, computed ${ computedMd }`))
        }
      }
    },
  })
}
