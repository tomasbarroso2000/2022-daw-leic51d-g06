# Use the official nginx image as the base image
FROM nginx

# Create a new directory for the project
RUN mkdir -p /usr/src/app

# Set the working directory to the new directory
WORKDIR /usr/src/app

# Copy the necessary files to the working directory
COPY public .
COPY dist .

# Expose the app's port
EXPOSE 8083