# FilePicker [![](https://jitpack.io/v/VijayMakwana/FilePicker.svg)](https://jitpack.io/#VijayMakwana/FilePicker) [![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](https://opensource.org/licenses/Apache-2.0)

**Open camera app for capture photo or videos without asking any Camera or Storage Permission and
receive the captured file. Open gallery and pick one or multiple files without asking storage
permission. Apply mime type filters for picking files from gallery**

- Written in [**Kotlin**](http://kotlinlang.org)

- Support Android Kotlin Projects

- Minimum API Level support 21

- Kotlin DSL patterns with very minimal code

- kotlin lambda callbacks for loading, success (with file), error (with message)

- Efficient and easy API

- Tiny in size

### Gradle

Add this in your project level `build.gradle`

  ```gradle
  allprojects {
   repositories {
          maven { url "https://jitpack.io" } // add this line
      }
  }
  ```

Then, add the library to your module `build.gradle`

  ```
  dependencies {
   implementation 'com.github.VijayMakwana:FilePicker:0.0.4'
  }
  ```

## Usage

Open Camera and capture Image

  ```kotlin     
  captureImage {
            onSuccess { file ->
                // Here is your captured Image
            }
            onError { errorMessage->
                // You can show error message
            }
            onLoading {
                // you can show loading state here
            }
        }
  ```

Open Camera and capture Video

  ```kotlin     
  captureVideo {
            onSuccess { file ->
                // Here is your captured Video File
            }
            onError { errorMessage->
                // You can show error message
            }
            onLoading {
                // you can show loading state here
            }
        }
  ```

Open gallery and pick file

  ```kotlin     
  pickFile {
            onSuccess { file ->
                // Here is your picked file
            }
            onError { errorMessage->
                // You can show error message
            }
            onLoading {
                // you can show loading state here
            }
        }
  ```

Open gallery and pick multiple file

  ```kotlin     
  pickMultipleFile {
            onSuccess { fileList: List<File> ->
                // Here is your picked files
            }
            onError { errorMessage->
                // You can show error message
            }
            onLoading {
                // you can show loading state here
            }
        }
  ```

Open gallery and pick file with mime filter (e.g. you only want to pick photos and video no any
other files)

  ```kotlin     
  pickFile {
  					mimeTypes = arrayListOf("image/*", "video/*")
            onSuccess { file ->
                // Here is your picked file
            }
            onError { errorMessage->
                // You can show error message
            }
            onLoading {
                // you can show loading state here
            }
        }
  ```

