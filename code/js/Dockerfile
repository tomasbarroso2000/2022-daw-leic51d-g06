# Use the official Node.js image as the base image
FROM node:14

# Create a new directory for the project
RUN mkdir -p /usr/src/app

# Set the working directory to the new directory
WORKDIR /usr/src/app

# Copy the package.json file to the working directory
COPY package.json .

# Install the dependencies
RUN npm install

RUN npm install --save-dev webpack webpack-cli

RUN npm install --save-dev react

# Copy the rest of the project files to the working directory
RUN mkdir -p ./src
COPY src ./src/

RUN mkdir -p ./public
COPY public ./public/

COPY webpack.config.js .

COPY tsconfig.json .

# Expose the app's port
EXPOSE 8083

# Set the command to run when the container starts
CMD ["npm", "start"]
