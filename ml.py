# Imports
import pandas as pd
import numpy as np
import time
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import Pipeline
from sklearn.linear_model import SGDClassifier


def load_data():
    # Configure Filepaths
    filepath_dict = {'anxiety': 'res/classification_data/datasets/anxiety.csv',
                     'depression': 'res/classification_data/datasets/depression.csv',
                     'tourettes': 'res/classification_data/datasets/tourettes.csv'}

    df_list = []

    # Create the master-set
    for source, filepath in filepath_dict.items():
        df = pd.read_csv(filepath, names=['selftext'])
        df = df[df.selftext.notnull()]  # Remove empty values
        df = df[df.selftext != '']  # Remove empty strings
        df = df[df.selftext != '[deleted]']  # Remove deleted status posts
        df = df[df.selftext != '[removed]']  # Remove removed status posts
        df['category'] = source  # Add category column
        df_list.append(df)

    df = pd.concat(df_list)
    print(f'Summary: {df.info}\nDescription: {df.describe()}\nShape: {df.shape}')

    # Make master-set csv and save .csv file
    df.to_csv('data/master-set.csv', index=0)

    return df


def naive_bayes_classifier(df):
    # Extract features from files based on the 'bag-of-words' model
    text_clf = Pipeline([('vect', CountVectorizer()),
                         ('tfidf', TfidfTransformer()),
                         ('clf', MultinomialNB())])
    print("Features Extracted.")
    print("Term Frequencies Extracted.")

    # Run Naive Bayes(NB) ML Algorithm
    text_clf = text_clf.fit(df.selftext, df.category)

    # Test Performance of NB Classifier (Detailed)
    i = 0
    test_list = []
    shuffled_df = df.sample(frac=1)  # Get a random sample to use so that each illness/disorder is tested
    start_time = time.time()
    for selftext in shuffled_df.selftext:
        pred = text_clf.predict(shuffled_df.selftext)
        actual = shuffled_df.category[i]
        time_elapsed = (time.time() - start_time) / 60
        if pred[i] == actual:
            result = 'PASS'
        else:
            result = 'FAIL'
        test = f'ID: {i + 1}/{len(shuffled_df)} | Prediction: {pred[i]} | Actual: {actual} ' \
               f'| Result: {result} | Selftext: {selftext}'
        print(f'Time Elapsed: {time_elapsed:.2f}m | {test}')
        i = i + 1

        # Store Test into list
        test_list.append(test)

    total_time = (time.time() - start_time) / 60
    # Save test results to .csv
    test_df = pd.DataFrame(test_list, columns=['Results'])
    test_df.to_csv('data/test_results.csv', index=0)
    print("Detailed Testing Complete - test_results.csv created.")
    print(f"Total Time Elapsed: {total_time:.2f}m")

    # Test Performance of NB Classifier (General)
    predicted = text_clf.predict(df.selftext)
    print(f'Predicted: {predicted}')
    score = np.mean(predicted == df.category)
    print(f'Average Performance (Naive Bayes): {score}.')
    print("General Testing Complete.")


def main():
    print("Reached main().")
    # Load and consolidate the datasets
    df = load_data()
    print("Loaded dataframe.")

    # Run Naive Bayes(NB) Machine-learning Algorithm
    naive_bayes_classifier(df)


if __name__ == "__main__":
    main()
""""# Create feature matrix using bag-of-words model
sentences = df['selftext'].values
y = df['category'].values

le = LabelEncoder()  # Convert strings to number format
y = le.fit_transform(y)

sentences_train, sentences_test, y_train, y_test = train_test_split(sentences, y, test_size=0.20, random_state=10)

# Create the training and test set using the vectorized data
vectorizer = CountVectorizer()
vectorizer.fit(sentences_train)

X_train = vectorizer.transform(sentences_train)
X_test = vectorizer.transform(sentences_test)

# Setup logistic regression classification_data model and train
classifier = LogisticRegression()
classifier.fit(X_train, y_train)

score = classifier.score(X_test, y_test)
classifier.predict(y_test)
print("Accuracy: ", score)

# (Deep Learning) Keras Sequential Model
input_dim = X_train.shape[1]  # Number of features

# Create sequential model with TensorFlow background
model = Sequential()
model.add(layers.Dense(10, input_dim=input_dim, activation='relu'))
model.add(layers.Dense(1, activation='sigmoid'))
model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])
model.summary()

# Reset training
clear_session()


def plot_history(hist):
    print(history.history)

    acc = history.history['accuracy']
    val_acc = history.history['val_accuracy']
    loss = history.history['loss']
    val_loss = history.history['val_loss']
    x = range(1, len(acc) + 1)


    # Setup Plot
    plt.figure(figsize=(12, 5))
    plt.subplot(1, 2, 1)
    plt.plot(x, acc, 'b', label='Training acc')
    plt.plot(x, val_acc, 'r', label='Validation acc')
    plt.title('Training and validation accuracy')
    plt.legend()
    plt.subplot(1, 2, 2)
    plt.plot(x, loss, 'b', label='Training loss')
    plt.plot(x, val_loss, 'r', label='Validation loss')
    plt.title('Training and validation loss')
    plt.legend()
    plt.show()


# Gets the computed weights from the previous training
history = model.fit(X_train, y_train,
                    epochs=100,
                    verbose=False,
                    validation_data=(X_test, y_test),
                    batch_size=10)

# Evaluate accuracy of keras sequential model
loss, accuracy = model.evaluate(X_train, y_train, verbose=False)
print("Training Accuracy: {:.4f}".format(accuracy))
loss, accuracy = model.evaluate(X_test, y_test, verbose=False)
print("Testing Accuracy:  {:.4f}".format(accuracy))

model.save('model.pkl')
# Visualize the loss and accuracy for training and testing data
plt.style.use('ggplot')

plot_history(history)"""
