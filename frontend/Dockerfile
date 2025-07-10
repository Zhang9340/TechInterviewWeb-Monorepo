FROM node:18-alpine

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm install

COPY . .

ARG NEXT_PUBLIC_API_BASE
ARG NODE_ENV
ENV NEXT_PUBLIC_API_BASE=$NEXT_PUBLIC_API_BASE
ENV NODE_ENV=$NODE_ENV
#ENV NODE_ENV=development
RUN npm run build
# Expose port
EXPOSE 3000

# Run the development server
CMD ["npm", "run", "start"]
