const webpack = require('webpack')

module.exports = {
    mode: "production",
    resolve: {
        extensions: [".js", ".ts", ".tsx"]
    },
    devServer: {
        port: 8083,
        historyApiFallback: true,
        proxy: {
            "/api": {
                target: "http://localhost:8083",
                router: () => "http://localhost:8080"
            },
        },
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/
            }
        ]
    },
    plugins: [
        new webpack.DefinePlugin({
          'process.env.BASE_URL': undefined //|| JSON.stringify('http://192.168.1.85:8083'), // Replace the IP address with your private IP address
        })
    ]
}