var webpack = require('webpack');
var path = require('path');
var ExtractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
    entry: 
    	
            {
            	index : "./js/index.js",
            	conso: "./js/conso.js",
            	global: "./js/global.js",
            	about: "./js/about.js"
            }
            
    ,
    output: {
        path: '../../../target/classes/static/',
        filename: "[name]-bundle.js",
        publicPath: '/static/',
        contentBase: '/static'
    },
    module: {
        loaders: [
            {
                test: /\.jsx?$/,
                include: [
                    path.resolve(__dirname, "./src")
                ],
                loader: 'babel',
                query: {
                    presets: ['es2015', 'react']
                }
            },
            { 
            	test: /\.png$/, 
            	loader: "url-loader?limit=10000"
            },
            /* loaders for Font Awesome */
            { 
            	test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/, 
            	loader: "url-loader?limit=10000&mimetype=application/font-woff" 
            },
            { test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: "file-loader" },
           
            /* to ensure jQuery is loaded before Bootstrap */
            // Bootstrap 4
            { 
            	test: /bootstrap\/dist\/js\/umd\//, 
            	loaders: ["imports?jQuery=jquery", "bootstrap-loader"] 
            },

            //Bootstrap 3
            { 
            	test: /bootstrap-sass\/assets\/javascripts\//, 
            	loaders: ["imports?jQuery=jquery", "bootstrap-loader"] 
            },
            {
            	test: /\.scss$/, 
            	loaders: ["style", "css", "postcss", "sass"]
            },
            
            {
            	test: /\.css$/, 
            	loaders: ['style', 'css', 'postcss']
            },
            {
            	test: /\.csv$/, 
            	loader: 'dsv'
            },
            {
            	test: /\.json$/, 
            	loader: 'json'
            },

            {
                test: /\.js$/,
                loader: 'babel-loader',
                exclude: [/node_modules/],
                query: {
                  presets: [
                    "es2015",
                    "react",
                    "stage-0",
                  ]
                }
              }
        ]
    },
    plugins: [
        //new ExtractTextPlugin("[name].css"),
        new webpack.optimize.CommonsChunkPlugin({
            names: ["commons", "manifest"],
            minChunks: 3
        }),
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery"
        }),
        new webpack.ProvidePlugin({
            $: "react-dom",
            ReactDOM: "react-dom"
        }),
        new webpack.HotModuleReplacementPlugin()
    ]
};
