
#### 1. Project Overview

"Grass Insight Evening Language" is a smart application designed to help users better understand plants. Through a chatbot, users can quickly identify plant types via text or images and learn about their growth conditions and care methods. The blog covers articles related to planting techniques and offers a real-time Q&A feature. The image recognition function allows quick access to encyclopedic information and symbolic meanings of plants. Users can also listen to others' stories about cultivating plants. With these features, "Grass Insight Evening Language" helps users delve deeper into each plant's lifestyle, enhancing their plant care skills and fostering a love for plants. The app can handle Q&A for over 90 plant types.

#### 2. Technical Architecture

##### 1. Frontend

The app uses Android (Java) for frontend development, defining interface layouts through XML files and handling logic and UI updates in Activities and Fragments.

##### 2. Backend

###### Standard Q&A

Utilizes OpenAI's API to facilitate communication with backend services, obtaining chatbot responses.

###### Plant-related Q&A

Backend Interface Description:

- **POST /predict**:
  - Sends POST requests to a Tencent Cloud server, using a model deployed on the server for text classification and knowledge base queries based on the user's question and specified plant type, returning relevant information.
  
  - Request Parameters:
    - question: User's query about plant growth habits, soil requirements, water needs, etc.
    - species: Specified plant type by the user.
  
  - Request Example:
    ```json
    {"question": "What are the growth habits of the plant?", "species": "Rose"}
    ```
  
  - Response Example:
    ```json
    {"growth_habits": "Roses have growth habits that...", "soil_requirements": "Roses require soil that...", "water_needs": "Roses need water levels that..."}
    ```

##### 3. Communication

Uses OkHttpClient library for network requests, including sending requests to OpenAI's API and handling responses for the plant recognition service.

##### 4. Database

Employs Room library to create and manage a local database, storing chat records and other data. SQLite manages backend functions such as login and registration.

##### 5. Third-party Services

###### PlantNet API Service

The app uses the PlantNet API for plant identification, which supports identifying plant types by uploading images and provides detailed plant information.

- **Request Parameters**:
  - project (string, path): In which reference system to search for the plant (use one of the available projects or "all" for the most relevant project results). Default value: all
  - include-related-images (boolean, query): If true, returns the most similar images for each possible species. Default value: false
  - no-reject (boolean, query): Disables "no result" in cases of rejected class matches. Default value: false
  - lang (string, query): Internationalization (default: en).
  - type (string, query): Model type: use "kt" for the new POWO / WGSRPD based plant area and recognition engine (2023+), "legacy" for traditional plant area and recognition engine (2022).
  - api-key (string, query): Your private API key.
  - authenix-access-token (string, query): Authenix access token.
  - images (array[file], formData): Images (all images must represent the same plant).
  - organs (array[string], formData): Organs related to images.

- **Response Format**:
  - Content-Type: application/json

- **Successful Response Example (200)**:
  ```json
  {
    "query": {
      "project": "all",
      "images": ["base64_encoded_image_string"],
      "organs": ["leaf"],
      "includeRelatedImages": true,
      "noReject": true,
      "language": "zh",
      "preferedReferential": "plants",
      "switchToProject": "none",
      "bestMatch": "Rosa chinensis",
      "results": [
        {
          "score": 0.95,
          "species": {
            "scientificNameWithoutAuthor": "Rosa chinensis",
            "scientificNameAuthorship": "Jacq.",
            "scientificName": "Rosa chinensis Jacq.",
            "genus": {
              "scientificNameWithoutAuthor": "Rosa",
              "scientificNameAuthorship": "L.",
              "scientificName": "Rosa L."
            },
            "family": {
              "scientificNameWithoutAuthor": "Rosaceae",
              "scientificNameAuthorship": "Juss.",
              "scientificName": "Rosaceae Juss."
            },
            "commonNames": ["China Rose", "Chinese Rose"],
            "images": [
              {
                "organ": "leaf",
                "author": "John Doe",
                "license": "CC BY-SA 4.0",
                "date": {
                  "timestamp": 1594857600,
                  "string": "2020-07-16"
                },
                "citation": "Doe, J. (2020). China Rose. CC BY-SA 4.0.",
                "url": {
                  "o": "https://example.com/image_o.jpg",
                  "m": "https://example.com/image_m.jpg",
                  "s": "https://example.com/image_s.jpg"
                }
              }
            ],
            "gbif": {
              "id": 2891234
            },
            "powo": {
              "id": "urn:lsid.org:names:123456-1"
            },
            "iucn": {
              "id": "12345",
              "category": "LC"
            }
          }
        }
      ],
      "remainingIdentificationRequests": 99,
      "version": "1.0"
    }
  }
  ```

#### 3. Feature Implementation

##### 3.1 Chatbot

- Recognition and Care Guide: Users input questions
- Model Building: Train a Q&A model specific to plant information and species, deployed on Tencent Cloud lightweight server
  - Model Building Details:
    - Aimed at providing plant care information. Through user input of plant type and questions, the system calls the backend interface, returning corresponding care advice and measures. The model loads two datasets (see attachments) Updated_Info_Dataset.csv and complex_plant_x_questions.csv, parses and cleans data, combines questions, information, and measures into a comprehensive text feature, vectorizes using TfidfVectorizer, and processes labels with MultiLabelBinarizer. The model uses OneVsRestClassifier with LogisticRegression for multi-label classification training, predicting multiple care categories based on input questions.
  - Core functions:
    - Multi-label classification: Input a question, the model predicts multiple care categories;
    - Most Similar Question Matching: Finds the most similar question in the database and returns its corresponding categories and species;
    - Most Similar Species Matching: Finds the most similar species name in the database;
    - Retrieval of Care Information and Measures: Extracts corresponding care information and measures from the database. The front-end page allows users to input plant type and question, click submit to call the backend interface /predict, returning the most similar questions, categories, species, and corresponding care information and measures.
  - Deployment: A Flask API provides a RESTful service, allowing users to submit questions and species names via POST request, and the system returns relevant information. If no match is found, an error message is returned. Configure and start the Flask application, considering using tools like Nginx as a reverse proxy to enhance security and performance.
  - Maintenance: Regularly monitor server resource usage, record application logs, and perform data backups to prevent data loss. Regularly update systems and libraries, optimize code and database queries, and enhance security measures to ensure application stability, performance, and security.

##### 3.2 Blog Function

- Mainly includes displaying articles on planting techniques and offers real-time Q&A.
  - Displaying planting technique articles: Create a blog module in the app where users can browse various articles on plant care and cultivation tips. These articles should contain detailed steps, precautions, and practical tips to help users better understand plant care.
  - Providing real-time Q&A: Add a real-time Q&A feature in the blog module, where users can post questions and receive immediate answers. This could be a text-based interaction interface where users enter questions and wait for the system to provide answers. Answers can be generated based on the user-provided questions and blog article content.
  - Blog Function Implementation Details:
    - Create a database for blog articles, storing articles on various plant cultivation tips and care experiences.
    - Implement an interface for displaying these articles, providing functions for user comments and feedback.
    - Create a chatbot for the real-time Q&A feature, generating answers based on user-provided questions and blog articles.
    - Ensure system response speed and accuracy to provide a good user experience.

##### 3.3 Image Recognition Function

- Quickly obtains encyclopedic information and symbolic meanings of plants through image recognition:
  - Users upload plant images, and the app uses the PlantNet API for plant recognition.
  - Returns recognition results, including the plant's scientific name, common names, classification information, related images, and encyclopedic information and symbolic meanings.
  - Image Recognition Function Implementation Details:
    - Users upload plant images to the app, which sends the images to the PlantNet API for recognition.
    - The PlantNet API returns the recognition results, including the plant's scientific name, common names, classification information, related images, and encyclopedic information and symbolic meanings.
    - The app displays the recognition results to the user and provides related encyc

lopedic information and symbolic meanings.
    - Ensure the system's recognition accuracy and response speed to provide a good user experience.
    - Implementation Details:
      - Use the Retrofit library in the Android app to communicate with the PlantNet API.
      - After users upload plant images, the app converts the images into Base64 encoded strings and sends them via POST request to the PlantNet API.
      - After the PlantNet API returns the recognition results, the app parses the JSON data and displays the recognition results to the user.
      - The app displays related encyclopedic information and symbolic meanings on the interface, allowing users to browse and learn more about the plants.

##### 3.4 Voice Story Sharing Function

- Users can also listen to others' plant cultivation stories:
  - Users can upload their own recorded plant cultivation story audio files.
  - Other users can listen to these audio files to learn about others' plant cultivation experiences and stories.
  - Voice Story Sharing Function Implementation Details:
    - Users record and upload plant cultivation story audio files to the app.
    - The app stores the audio files on the server and generates a unique link.
    - Other users can play these audio files through the app's audio player, listening to others' shared plant cultivation stories.
    - Ensure the system's audio file storage and playback functionality's stability and smoothness to provide a good user experience.

#### 4. Testing and Deployment

##### Testing

1. **Functional Testing**: Perform unit and integration testing for each function module to ensure correctness and stability.
2. **Performance Testing**: Conduct stress tests, simulating high concurrent requests to ensure system response speed and stability under high load.
3. **Security Testing**: Check the system's security to prevent common security vulnerabilities like SQL injection and XSS attacks.
4. **User Experience Testing**: Invite users to participate in experience testing, collect feedback, and make improvements.

##### Deployment

1. **Server Environment Configuration**: Install and configure the required environment and dependencies on the server, such as Java JDK, Android SDK, Flask, Nginx, etc.
2. **Code Deployment**: Upload the frontend and backend code to the server and perform necessary configurations and adjustments.
3. **Database Configuration**: Configure and manage the database on the server to ensure data security and consistency.
4. **Service Startup**: Start frontend and backend services and perform necessary debugging and optimization to ensure system stability and performance.
5. **Continuous Monitoring**: Configure monitoring tools like Grafana and Prometheus to monitor the system's operating conditions in real time, promptly identify and address issues.

#### 5. Maintenance and Updates

1. **Regular Updates**: Regularly update the system and dependencies, fix known bugs and vulnerabilities, and improve system performance and security.
2. **User Feedback**: Collect user feedback, make timely improvements and optimizations, and enhance user experience.
3. **Data Backup**: Regularly back up the database and important data to prevent data loss.
4. **Performance Optimization**: Continuously optimize code and database queries to improve system response speed and performance.
5. **Security Strengthening**: Regularly conduct security checks and fortifications to prevent system attacks and intrusions.

#### 6. Project Prospects

1. **Feature Expansion**: Continuously expand and enhance system functions based on user needs and feedback, such as adding more plant types and information, optimizing recognition algorithms, adding multilingual support, etc.
2. **User Community**: Build a user community to facilitate user interaction and sharing of plant care experiences and knowledge.
3. **Commercialization**: Explore commercialization models, such as offering premium membership services, partnering with plant businesses, etc., to enhance the project's sustainability.
