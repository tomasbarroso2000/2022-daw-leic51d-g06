module.exports = {
    mode: "development",
    resolve: {
        extensions: [".js", ".ts", ".tsx"]
    },
    devServer: {
        port: 8083,
        historyApiFallback: true,
        proxy: {
            "/api": {
                target: "http://localhost:8083",
                router: () => process.env.SERVER || "http://localhost:8080"
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
    }
}